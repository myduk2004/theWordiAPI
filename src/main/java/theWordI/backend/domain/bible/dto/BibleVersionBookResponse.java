package theWordI.backend.domain.bible.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import theWordI.backend.domain.bible.entity.BibleVersion;

import java.util.List;

@Getter
@AllArgsConstructor
public class BibleVersionBookResponse {
    private List<BibleVersion> versions;
    private List<BibleBookResponse> books;
}
