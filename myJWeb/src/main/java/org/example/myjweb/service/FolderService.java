package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.Folder;
import org.example.myjweb.repository.FolderMapper;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private final FolderMapper folderMapper;

    public FolderService(FolderMapper folderMapper) {
        this.folderMapper = folderMapper;
    }

    public Folder create(Folder folder) {
        ServiceValidator.requireObject(folder, "目录不能为空");
        ServiceValidator.requireId(folder.getCollectionId(), "集合ID不能为空");
        ServiceValidator.requireText(folder.getName(), "目录名称不能为空");
        folder.setId(null);
        folder.setName(folder.getName().trim());
        if (folder.getSortOrder() == null) {
            folder.setSortOrder(0);
        }
        folderMapper.insert(folder);
        return folder;
    }

    public Optional<Folder> findById(Long id) {
        ServiceValidator.requireId(id, "目录ID不能为空");
        return Optional.ofNullable(folderMapper.selectById(id));
    }

    public List<Folder> listByCollectionId(Long collectionId) {
        ServiceValidator.requireId(collectionId, "集合ID不能为空");
        return folderMapper.selectList(new LambdaQueryWrapper<Folder>()
                .eq(Folder::getCollectionId, collectionId)
                .orderByAsc(Folder::getSortOrder, Folder::getId));
    }

    public List<Folder> listByParent(Long collectionId, Long parentId) {
        ServiceValidator.requireId(collectionId, "集合ID不能为空");
        LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<Folder>()
                .eq(Folder::getCollectionId, collectionId)
                .orderByAsc(Folder::getSortOrder, Folder::getId);
        if (parentId == null) {
            wrapper.isNull(Folder::getParentId);
        } else {
            wrapper.eq(Folder::getParentId, parentId);
        }
        return folderMapper.selectList(wrapper);
    }

    public boolean update(Folder folder) {
        ServiceValidator.requireObject(folder, "目录不能为空");
        ServiceValidator.requireId(folder.getId(), "目录ID不能为空");
        if (folder.getName() != null) {
            ServiceValidator.requireText(folder.getName(), "目录名称不能为空");
            folder.setName(folder.getName().trim());
        }
        return folderMapper.updateById(folder) > 0;
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "目录ID不能为空");
        return folderMapper.deleteById(id) > 0;
    }
}
