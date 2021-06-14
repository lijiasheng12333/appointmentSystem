package com.ljs.appointment.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljs.appointment.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);
}
