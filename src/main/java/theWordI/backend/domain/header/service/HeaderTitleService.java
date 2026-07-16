package theWordI.backend.domain.header.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.header.dto.HeaderTitleResponse;
import theWordI.backend.domain.header.repository.HeaderTitleRepository;
import theWordI.backend.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HeaderTitleService {

    private final HeaderTitleRepository repo;

    /*
    헤더 말씀정보 가져오기
   */
    public List<HeaderTitleResponse> getdHeaderTitle()
    {
        Long userId = SecurityUtil.getUserIdExc();
        if (userId == null)
        {
            return repo.findHeaderCommonTitles();
        }

        List<HeaderTitleResponse> headerTitles = repo.findHeaderTitles(userId);

        if (headerTitles.isEmpty()) {
            return repo.findHeaderCommonTitles();
        }

        return headerTitles;

    }

}
