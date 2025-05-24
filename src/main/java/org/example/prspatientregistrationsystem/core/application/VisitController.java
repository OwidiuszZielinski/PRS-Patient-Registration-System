package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.core.visit.VisitDto;
import org.example.prspatientregistrationsystem.core.visit.VisitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visit/")
@CrossOrigin(origins = "http://localhost:3000")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public void addVisit(@RequestBody VisitDto visitDto) {
        visitService.addVisit(visitDto);
    }

    @GetMapping
    public List<VisitDto> getVisits() {
        return visitService.findAll();
    }

    @DeleteMapping(path = "{id}/")
    public void delete(@PathVariable Long id) {
        visitService.delete(id);
    }
}
