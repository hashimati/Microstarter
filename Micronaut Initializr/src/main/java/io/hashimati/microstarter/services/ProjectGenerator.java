package io.hashimati.microstarter.services;

import io.hashimati.microstarter.entity.micronaut.ProjectRequest;

import java.io.File;
/**
 * @author Ahmed Al Hashmi @hashimati
 */


public interface ProjectGenerator {

    public File generateProject(ProjectRequest projectRequest) throws Exception;
}
