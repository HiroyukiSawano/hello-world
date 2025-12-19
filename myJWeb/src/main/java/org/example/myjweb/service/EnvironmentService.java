package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.Environment;
import org.example.myjweb.repository.EnvironmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnvironmentService {

    private final EnvironmentMapper environmentMapper;

    public EnvironmentService(EnvironmentMapper environmentMapper) {
        this.environmentMapper = environmentMapper;
    }

    @Transactional
    public Environment create(Environment environment) {
        ServiceValidator.requireObject(environment, "环境不能为空");
        ServiceValidator.requireId(environment.getProjectId(), "项目ID不能为空");
        ServiceValidator.requireText(environment.getName(), "环境名称不能为空");
        environment.setId(null);
        environment.setName(environment.getName().trim());
        if (environment.getIsDefault() == null) {
            environment.setIsDefault(0);
        }
        environmentMapper.insert(environment);
        if (environment.getIsDefault() == 1) {
            // 保证同一项目只有一个默认环境
            Environment reset = new Environment();
            reset.setIsDefault(0);
            environmentMapper.update(reset, new LambdaQueryWrapper<Environment>()
                    .eq(Environment::getProjectId, environment.getProjectId())
                    .ne(Environment::getId, environment.getId()));
        }
        return environment;
    }

    public Optional<Environment> findById(Long id) {
        ServiceValidator.requireId(id, "环境ID不能为空");
        return Optional.ofNullable(environmentMapper.selectById(id));
    }

    public List<Environment> listByProjectId(Long projectId) {
        ServiceValidator.requireId(projectId, "项目ID不能为空");
        return environmentMapper.selectList(new LambdaQueryWrapper<Environment>()
                .eq(Environment::getProjectId, projectId)
                .orderByAsc(Environment::getId));
    }

    public boolean update(Environment environment) {
        ServiceValidator.requireObject(environment, "环境不能为空");
        ServiceValidator.requireId(environment.getId(), "环境ID不能为空");
        if (environment.getName() != null) {
            ServiceValidator.requireText(environment.getName(), "环境名称不能为空");
            environment.setName(environment.getName().trim());
        }
        if (environment.getIsDefault() != null && environment.getIsDefault() == 1) {
            setDefault(environment.getId());
            environment.setIsDefault(null);
        }
        return environmentMapper.updateById(environment) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "环境ID不能为空");
        return environmentMapper.deleteById(id) > 0;
    }

    @Transactional
    public boolean setDefault(Long environmentId) {
        ServiceValidator.requireId(environmentId, "环境ID不能为空");
        Environment environment = environmentMapper.selectById(environmentId);
        if (environment == null) {
            return false;
        }
        // 重置同项目默认环境，再设置当前环境
        Environment reset = new Environment();
        reset.setIsDefault(0);
        environmentMapper.update(reset, new LambdaQueryWrapper<Environment>()
                .eq(Environment::getProjectId, environment.getProjectId()));
        Environment update = new Environment();
        update.setId(environmentId);
        update.setIsDefault(1);
        return environmentMapper.updateById(update) > 0;
    }
}
