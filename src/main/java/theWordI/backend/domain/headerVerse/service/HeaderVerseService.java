package theWordI.backend.domain.headerVerse.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.headerVerse.dto.HeaderVerseResponse;
import theWordI.backend.domain.headerVerse.entity.HeaderVerse;
import theWordI.backend.domain.headerVerse.repository.HeaderVerseRepository;
import theWordI.backend.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HeaderVerseService {

    private final HeaderVerseRepository repo;

    /*
    헤더 말씀정보 가져오기
   */
    public List<HeaderVerseResponse> getdHeaderVerses()
    {
       return repo.findHeaderVerses(SecurityUtil.getUserId());
    }

}
