package com.example.dictionary.service;

import com.example.dictionary.entity.Group;
import com.example.dictionary.repository.BasicIdentityRepository;
import com.example.dictionary.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

    @Autowired
    private BasicIdentityService basicIdentityService;

    @Autowired
    private AuthenticationService authenticationService;

    public List<Map<String, Object>> listWithCounts() {
        List<Group> groups = groupRepository.findAll();
        List<Map<String, Object>> out = new ArrayList<>(groups.size());
        for (Group g : groups) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", g.getId());
            row.put("l15Name", g.getL15Name());
            row.put("l2Name", g.getL2Name());
            row.put("appCount", basicIdentityRepository.countByAssignmentGroup(g.getL2Name()));
            out.add(row);
        }
        return out;
    }

    public List<String> getDistinctL2Names() {
        return groupRepository.findDistinctL2Names();
    }

    public Group add(String l15Name, String l2Name) {
        return groupRepository.save(new Group(l15Name, l2Name));
    }

    public boolean exists(String l15Name, String l2Name) {
        return groupRepository.findByL15NameAndL2Name(l15Name, l2Name).isPresent();
    }

    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional
    public Group update(Long id, String l15Name, String l2Name) {
        Group g = groupRepository.findById(id).orElseThrow();
        String oldL2 = g.getL2Name();
        g.setL15Name(l15Name);
        g.setL2Name(l2Name);
        Group saved = groupRepository.save(g);

        if (oldL2 != null && !oldL2.isBlank() && !oldL2.equals(l2Name)) {

            if (groupRepository.countByL2Name(oldL2) == 0) {
                basicIdentityService.renameAssignmentGroup(oldL2, l2Name);
                authenticationService.renameGroupInAllFilters(oldL2, l2Name);
            }
        }
        return saved;
    }

    public boolean delete(Long id) {
        if (!groupRepository.existsById(id)) return false;
        groupRepository.deleteById(id);
        return true;
    }
}
