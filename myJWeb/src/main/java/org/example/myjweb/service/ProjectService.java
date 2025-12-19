package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import org.example.myjweb.entity.Project;
import org.example.myjweb.repository.ProjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;

    public ProjectService(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public Project create(Project project) {
        ServiceValidator.requireObject(project, "项目不能为空");
        ServiceValidator.requireText(project.getName(), "项目名称不能为空");
        project.setId(null);
        project.setName(project.getName().trim());
        if (project.getDescription() != null) {
            project.setDescription(StringUtils.trimWhitespace(project.getDescription()));
        }
        projectMapper.insert(project);
        return project;
    }

    public Optional<Project> findById(Long id) {
        ServiceValidator.requireId(id, "项目ID不能为空");
        return Optional.ofNullable(projectMapper.selectById(id));
    }

    public List<Project> listAll() {
        return projectMapper.selectList(null);
    }

    public boolean update(Project project) {
        ServiceValidator.requireObject(project, "项目不能为空");
        ServiceValidator.requireId(project.getId(), "项目ID不能为空");
        if (project.getName() != null) {
            ServiceValidator.requireText(project.getName(), "项目名称不能为空");
            project.setName(project.getName().trim());
        }
        if (project.getDescription() != null) {
            project.setDescription(StringUtils.trimWhitespace(project.getDescription()));
        }
        return projectMapper.updateById(project) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "项目ID不能为空");
        return projectMapper.deleteById(id) > 0;
    }
}
