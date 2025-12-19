package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.ApiCollection;
import org.example.myjweb.repository.ApiCollectionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ApiCollectionService {

    private final ApiCollectionMapper apiCollectionMapper;

    public ApiCollectionService(ApiCollectionMapper apiCollectionMapper) {
        this.apiCollectionMapper = apiCollectionMapper;
    }

    public ApiCollection create(ApiCollection collection) {
        ServiceValidator.requireObject(collection, "集合不能为空");
        ServiceValidator.requireId(collection.getProjectId(), "项目ID不能为空");
        ServiceValidator.requireText(collection.getName(), "集合名称不能为空");
        collection.setId(null);
        collection.setName(collection.getName().trim());
        if (collection.getDescription() != null) {
            collection.setDescription(StringUtils.trimWhitespace(collection.getDescription()));
        }
        if (collection.getSortOrder() == null) {
            collection.setSortOrder(0);
        }
        apiCollectionMapper.insert(collection);
        return collection;
    }

    public Optional<ApiCollection> findById(Long id) {
        ServiceValidator.requireId(id, "集合ID不能为空");
        return Optional.ofNullable(apiCollectionMapper.selectById(id));
    }

    public List<ApiCollection> listByProjectId(Long projectId) {
        ServiceValidator.requireId(projectId, "项目ID不能为空");
        return apiCollectionMapper.selectList(new LambdaQueryWrapper<ApiCollection>()
                .eq(ApiCollection::getProjectId, projectId)
                .orderByAsc(ApiCollection::getSortOrder, ApiCollection::getId));
    }

    public boolean update(ApiCollection collection) {
        ServiceValidator.requireObject(collection, "集合不能为空");
        ServiceValidator.requireId(collection.getId(), "集合ID不能为空");
        if (collection.getName() != null) {
            ServiceValidator.requireText(collection.getName(), "集合名称不能为空");
            collection.setName(collection.getName().trim());
        }
        if (collection.getDescription() != null) {
            collection.setDescription(StringUtils.trimWhitespace(collection.getDescription()));
        }
        return apiCollectionMapper.updateById(collection) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "集合ID不能为空");
        return apiCollectionMapper.deleteById(id) > 0;
    }
}
