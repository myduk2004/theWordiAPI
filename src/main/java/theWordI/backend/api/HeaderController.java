package theWordI.backend.api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theWordI.backend.domain.header.dto.HeaderTitleResponse;
import theWordI.backend.domain.header.service.HeaderTitleService;

import java.util.List;

@RequestMapping("/header")
@RestController
@RequiredArgsConstructor
public class HeaderController {

    private final HeaderTitleService svc;

    //헤더 성경구절 조회
    @GetMapping("/titles")
    public ResponseEntity<List<HeaderTitleResponse>> getHeaderTitles()
    {

        List<HeaderTitleResponse> response = svc.getdHeaderTitle();
        return ResponseEntity.ok(response);
    }

}
