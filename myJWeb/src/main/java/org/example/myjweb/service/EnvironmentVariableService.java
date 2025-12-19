package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.EnvironmentVariable;
import org.example.myjweb.repository.EnvironmentVariableMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EnvironmentVariableService {

    private final EnvironmentVariableMapper environmentVariableMapper;

    public EnvironmentVariableService(EnvironmentVariableMapper environmentVariableMapper) {
        this.environmentVariableMapper = environmentVariableMapper;
    }

    public EnvironmentVariable create(EnvironmentVariable variable) {
        ServiceValidator.requireObject(variable, "环境变量不能为空");
        ServiceValidator.requireId(variable.getEnvironmentId(), "环境ID不能为空");
        ServiceValidator.requireText(variable.getName(), "变量名称不能为空");
        variable.setId(null);
        variable.setName(variable.getName().trim());
        if (variable.getValue() != null) {
            variable.setValue(StringUtils.trimWhitespace(variable.getValue()));
        }
        if (variable.getIsSecret() == null) {
            variable.setIsSecret(0);
        }
        if (variable.getIsEnabled() == null) {
            variable.setIsEnabled(1);
        }
        if (variable.getSortOrder() == null) {
            variable.setSortOrder(0);
        }
        environmentVariableMapper.insert(variable);
        return variable;
    }

    public Optional<EnvironmentVariable> findById(Long id) {
        ServiceValidator.requireId(id, "环境变量ID不能为空");
        return Optional.ofNullable(environmentVariableMapper.selectById(id));
    }

    public List<EnvironmentVariable> listByEnvironmentId(Long environmentId) {
        ServiceValidator.requireId(environmentId, "环境ID不能为空");
        return environmentVariableMapper.selectList(new LambdaQueryWrapper<EnvironmentVariable>()
                .eq(EnvironmentVariable::getEnvironmentId, environmentId)
                .orderByAsc(EnvironmentVariable::getSortOrder, EnvironmentVariable::getId));
    }

    public boolean update(EnvironmentVariable variable) {
        ServiceValidator.requireObject(variable, "环境变量不能为空");
        ServiceValidator.requireId(variable.getId(), "环境变量ID不能为空");
        if (variable.getName() != null) {
            ServiceValidator.requireText(variable.getName(), "变量名称不能为空");
            variable.setName(variable.getName().trim());
        }
        if (variable.getValue() != null) {
            variable.setValue(StringUtils.trimWhitespace(variable.getValue()));
        }
        return environmentVariableMapper.updateById(variable) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "环境变量ID不能为空");
        return environmentVariableMapper.deleteById(id) > 0;
    }
}
