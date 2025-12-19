package org.example.myjweb.service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.myjweb.entity.RequestFile;
import org.example.myjweb.repository.RequestFileMapper;
import org.springframework.stereotype.Service;

@Service
public class RequestFileService {

    private final RequestFileMapper requestFileMapper;

    public RequestFileService(RequestFileMapper requestFileMapper) {
        this.requestFileMapper = requestFileMapper;
    }

    public RequestFile create(RequestFile file) {
        ServiceValidator.requireObject(file, "请求附件不能为空");
        ServiceValidator.requireId(file.getRequestVersionId(), "请求版本ID不能为空");
        ServiceValidator.requireText(file.getFieldName(), "表单字段名不能为空");
        ServiceValidator.requireText(file.getFileName(), "文件名不能为空");
        if (file.getContent() == null) {
            throw new IllegalArgumentException("文件内容不能为空");
        }
        file.setId(null);
        file.setFieldName(file.getFieldName().trim());
        file.setFileName(file.getFileName().trim());
        if (file.getSize() == null) {
            // 根据内容长度补齐文件大小
            file.setSize((long) file.getContent().length);
        }
        if (file.getEnabled() == null) {
            file.setEnabled(1);
        }
        if (file.getSortOrder() == null) {
            file.setSortOrder(0);
        }
        requestFileMapper.insert(file);
        return file;
    }

    public Optional<RequestFile> findById(Long id) {
        ServiceValidator.requireId(id, "请求附件ID不能为空");
        return Optional.ofNullable(requestFileMapper.selectById(id));
    }

    public List<RequestFile> listByRequestVersionId(Long requestVersionId) {
        ServiceValidator.requireId(requestVersionId, "请求版本ID不能为空");
        return requestFileMapper.selectList(new LambdaQueryWrapper<RequestFile>()
                .eq(RequestFile::getRequestVersionId, requestVersionId)
                .orderByAsc(RequestFile::getSortOrder, RequestFile::getId));
    }

    public boolean delete(Long id) {
        ServiceValidator.requireId(id, "请求附件ID不能为空");
        return requestFileMapper.deleteById(id) > 0;
    }
}
