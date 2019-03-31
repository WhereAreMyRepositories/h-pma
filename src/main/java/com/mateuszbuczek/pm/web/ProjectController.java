package com.mateuszbuczek.pm.web;

import com.mateuszbuczek.pm.domain.Project;
import com.mateuszbuczek.pm.services.ProjectService;
import com.mateuszbuczek.pm.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ValidationService validationService;

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal) {

        ResponseEntity<?> errorMap = validationService.validate(result);
        if(errorMap != null) return errorMap;

        projectService.saveOrUpdate(project, principal.getName());
        return new ResponseEntity<Project>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectIdentifier}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectIdentifier, Principal principal) {
        Project project = projectService.findProjectByIdentifier(projectIdentifier, principal.getName());

        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllProjects(Principal principal) {
        return projectService.findAllProjects(principal.getName());
    }

    @DeleteMapping("/{projectIdentifier}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectIdentifier, Principal principal) {

        projectService.deleteProjectByIdentifier(projectIdentifier, principal.getName());

        return  new ResponseEntity<String>("Project with ID: '" + projectIdentifier + "' was deleted", HttpStatus.OK);
    }
}
