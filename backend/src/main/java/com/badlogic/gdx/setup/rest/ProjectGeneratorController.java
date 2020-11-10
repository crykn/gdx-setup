package com.badlogic.gdx.setup.rest;

import com.badlogic.gdx.setup.DependencyBank;
import com.badlogic.gdx.setup.ProjectGeneratorService;
import com.badlogic.gdx.setup.ProjectGeneratorService.CachedProjects;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;
import com.badlogic.gdx.setup.backend.GeneratorResponse;
import com.badlogic.gdx.setup.backend.VersionResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ProjectGeneratorController {
    private final ProjectGeneratorService service;

    public ProjectGeneratorController(ProjectGeneratorService service) {
        this.service = service;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadZipFile(@PathVariable String id) {

        CachedProjects zipFile = service.getZipFile(id);

        if (zipFile == null)
            throw new NotFoundException("Project not found");

        ByteArrayResource bar = new ByteArrayResource(zipFile.zippedContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "libgdxproject" + ".zip" + "\"")
                .body(bar);

    }

    @GetMapping("/generate")
    public GeneratorResponse generateProject(GenerateProjectParams projectData) {

        GeneratorResponse response = new GeneratorResponse();

        try {
            response.downloadUrl = service.generateAndZipGdxProject(projectData);
            response.warnings = projectData.warnings.toArray(new String[0]);
        } catch (Throwable t) {
            String errorMessage = t.getMessage();
            response.errorMessage = errorMessage != null && !errorMessage.isEmpty() ?
                    "Error occured: " + errorMessage :
                    "Error occured: " + t.getClass().getName();
            t.printStackTrace();
        }

        return response;
    }

    @PostMapping("/generate")
    public GeneratorResponse generateProjectExtended(@RequestBody GenerateProjectParams projectData) {
        return generateProject(projectData);
    }

    @GetMapping("/versions")
    public VersionResponse getVersions() {
        VersionResponse response = new VersionResponse();

        response.backendVersion = ProjectGeneratorService.GENERATOR_VERSION;
        response.supportedGdxVersions = new String[]{ProjectGeneratorService.GENERATED_VERSION};

        List<String> dependencies = new ArrayList<>(DependencyBank.ProjectDependency.values().length);
        for (DependencyBank.ProjectDependency pd : DependencyBank.ProjectDependency.values()) {
            dependencies.add(pd.name().toLowerCase());
        }
        response.availableExtensions = dependencies.toArray(new String[0]);


        return response;
    }

}
