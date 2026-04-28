package theWordI.backend.domain.readingPlan.entity;


import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum BookInfo {
    GEN(1, "창세기", 50),
    EXO(2, "출애굽기", 40),
    LEV(3, "레위기", 27),
    NUM(4, "민수기", 36),
    DEU(5, "신명기", 34),
    JOS(6, "여호수아기", 24),
    JDG(7, "사사기", 21),
    RUT(8,"룻기", 4),
    SA1(9,"사무엘기상", 31),
    SA2(10,"사무엘기하", 24),
    KI1(11,"열왕기상", 22),
    KI2(12,"열왕기하", 25),
    CH1(13,"역대기상", 29),
    CH2(14,"역대기하", 36),
    EZR(15,"에스라", 10),
    NEH(16,"느헤미야기", 13),
    EST(17,"에스더기", 10),
    JOB(18,"욥기", 42),
    PSA(19,"시편", 150),
    PRO(20,"잠언", 31),
    ECC(21,"전도서", 12),
    SNG(22,"솔로몬의 아가", 8),
    ISA(23,"이사야서", 66),
    JER(24,"예레미야서", 52),
    LAM(25,"예레미야 애가", 5),
    EZK(26,"에스겔서", 48),
    DAN(27,"다니엘서", 12),
    HOS(28,"호세아", 14),
    JOL(29,"요엘", 3),
    AMO(30,"아모스", 9),
    OBA(31,"오바댜", 1),
    JON(32,"요나", 4),
    MIC(33,"미가", 7),
    NAM(34,"나훔", 3),
    HAB(35,"하박국", 3),
    ZEP(36,"스바냐", 3),
    HAG(37,"학개", 2),
    ZEC(38,"스가랴", 14),
    MAL(39,"말라기", 4),
    MAT(40,"마태복음", 28),
    MRK(41,"마가복음", 16),
    LUK(42,"누가복음", 24),
    JHN(43,"요한복음", 21),
    ACT(44,"사도행전", 28),
    ROM(45,"로마서", 16),
    CO1(46,"고린도전서", 16),
    CO2(47,"고린도후서", 13),
    GAL(48,"갈라디아서", 6),
    EPH(49,"에베소서", 6),
    PHP(50,"빌립보서", 4),
    COL(51,"골로새서", 4),
    TH1(52,"데살로니가전서", 5),
    TH2(53,"데살로니가후서", 3),
    TI1(54,"디모데전서", 6),
    TI2(55,"디모데후서", 4),
    TIT(56,"디도서", 3),
    PHM(57,"빌레몬서", 1),
    HEB(58,"히브리서", 13),
    JAS(59,"야고보서", 5),
    PE1(60,"베드로전서", 5),
    PE2(61,"베드로후서", 3),
    JN1(62,"요한일서", 5),
    JN2(63,"요한이서", 1),
    JN3(64,"요한삼서", 1),
    JUD(65,"유다서", 1),
    REV(66,"요한계시록", 22);

    private final int bookId;
    private final String koreanName;
    private final int totalChapters;

    private static final Map<String, BookInfo> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(BookInfo::name, b -> b));

    private static final Map<Integer, BookInfo> ID_MAP =
            Stream.of(values()).collect(Collectors.toMap(BookInfo::getBookId, b -> b));

    BookInfo(int bookId, String koreanName, int totalChapters)
    {
        this.bookId = bookId;
        this.koreanName = koreanName;
        this.totalChapters = totalChapters;
    }


    public static BookInfo getByBookCode(String bookCode)
    {
        if (bookCode == null || bookCode.isBlank()) return null;
        return CODE_MAP.get(bookCode.toUpperCase());
    }



    public static BookInfo getByBookId(Integer bookId)
    {
        if (bookId == null ) return null;
        return ID_MAP.get(bookId);
    }


    public static int getTotalChapter(Integer bookId)
    {
        if (bookId == null ) return -1;

        BookInfo book = ID_MAP.get(bookId);
        return (book != null)?book.getTotalChapters(): -1;
    }


    public static int getTotalChapterByBookCode(String bookCode)
    {
        if (bookCode == null ) return -1;

        BookInfo book = CODE_MAP.get(bookCode);
        return (book != null)?book.getTotalChapters(): -1;
    }

}
