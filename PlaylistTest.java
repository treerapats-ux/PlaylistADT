import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PlaylistTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    public static void main(String[] args) {
        boolean assertsOn = false;
        assert assertsOn = true;
        if (!assertsOn) {
            System.out.println("WARNING: assertions disabled"
                    + " - re-run with: java -ea PlaylistTest\n");
        }

        System.out.println("=== Playlist Test Suite ===\n");

        testCreators();
        testAdd();
        testRemove();
        testObservers();
        testProducer();
        testExposure();

        System.out.println("\n=== Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total : " + (passed + failed));
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // --- Partition: ว่าง / มีเพลง / input ที่ผิดเงื่อนไข ---
    private static void testCreators() {
        System.out.println("-- Creators --");

        Playlist empty = new Playlist();
        check("new() -> empty", empty.size() == 0);
        check("new() -> contains nothing", !empty.contains("anything"));

        Playlist p = new Playlist(Arrays.asList("A", "B", "C"));
        check("new(list) -> size 3", p.size() == 3);
        check("new(list) -> contains B", p.contains("B"));
        check("new(list) -> preserves order",
                p.songs().equals(Arrays.asList("A", "B", "C")));

        // boundary: list ว่างคือขอบล่างที่ถูกต้อง
        Playlist fromEmpty = new Playlist(new ArrayList<String>());
        check("new(empty list) -> empty", fromEmpty.size() == 0);

        // input ที่ผิดเงื่อนไขต้องโยน exception ไม่ใช่ปล่อยผ่าน
        boolean threwDup = false;
        try {
            new Playlist(Arrays.asList("A", "A"));
        } catch (IllegalArgumentException e) {
            threwDup = true;
        }
        check("new(duplicates) -> throws IllegalArgumentException", threwDup);

        boolean threwNull = false;
        try {
            new Playlist(Arrays.asList("A", null));
        } catch (IllegalArgumentException e) {
            threwNull = true;
        }
        check("new(list with null) -> throws IllegalArgumentException", threwNull);

        boolean threwNullList = false;
        try {
            new Playlist(null);
        } catch (IllegalArgumentException e) {
            threwNullList = true;
        }
        check("new(null) -> throws IllegalArgumentException", threwNullList);
    }

    // --- Mutator: add ต้องรักษาลำดับและกันเพลงซ้ำ ---
    private static void testAdd() {
        System.out.println("\n-- Add --");

        Playlist s = new Playlist();
        check("add(A) -> returns true", s.add("A"));
        check("add(A) -> size 1", s.size() == 1);
        check("add(A) -> found by contains", s.contains("A"));

        s.add("B");
        s.add("C");
        check("add preserves insertion order",
                s.songs().equals(Arrays.asList("A", "B", "C")));

        // เพลงซ้ำไม่ใช่ error — คืน false เฉย ๆ
        check("add duplicate -> returns false", !s.add("A"));
        check("failed add leaves size unchanged", s.size() == 3);

        // input ที่ผิดเงื่อนไขต้องโยน exception
        boolean threwEmpty = false;
        try {
            s.add("");
        } catch (IllegalArgumentException e) {
            threwEmpty = true;
        }
        check("add(empty string) -> throws IllegalArgumentException", threwEmpty);

        boolean threwNull = false;
        try {
            s.add(null);
        } catch (IllegalArgumentException e) {
            threwNull = true;
        }
        check("add(null) -> throws IllegalArgumentException", threwNull);

        check("failed adds leave playlist unchanged", s.size() == 3);

        // boundary: เติมจนเต็มพอดีแล้วเติมเพิ่ม
        Playlist full = new Playlist();
        for (int i = 0; i < Playlist.MAX_SONGS; i++) {
            full.add("song" + i);
        }
        check("can fill up to MAX_SONGS", full.size() == Playlist.MAX_SONGS);
        check("add when full -> returns false", !full.add("one more"));
        check("full playlist stays at MAX_SONGS",
                full.size() == Playlist.MAX_SONGS);
    }

    // --- Mutator: remove ทั้งกรณีพบและไม่พบ ---
    private static void testRemove() {
        System.out.println("\n-- Remove --");

        Playlist s = new Playlist(Arrays.asList("A", "B", "C"));
        check("remove(B) -> returns true", s.remove("B"));
        check("remove -> size decreases", s.size() == 2);
        check("remove -> song is gone", !s.contains("B"));
        check("remove keeps the others in order",
                s.songs().equals(Arrays.asList("A", "C")));

        // ลบเพลงที่ไม่มีไม่ใช่ error — คืน false เฉย ๆ
        check("remove missing song -> returns false", !s.remove("nope"));
        check("failed remove leaves size unchanged", s.size() == 2);

        // boundary: ลบจนหมด
        s.remove("A");
        s.remove("C");
        check("remove all -> empty", s.size() == 0);
        check("remove on empty playlist -> returns false", !s.remove("A"));
    }

    // --- Observer ต้องไม่มี side effect ---
    private static void testObservers() {
        System.out.println("\n-- Observers --");

        Playlist s = new Playlist(Arrays.asList("A", "B"));
        check("size reports 2", s.size() == 2);
        check("contains finds an existing song", s.contains("A"));
        check("contains rejects a missing song", !s.contains("Z"));
        check("songs returns the full list in order",
                s.songs().equals(Arrays.asList("A", "B")));

        int before = s.size();
        s.size();
        s.contains("A");
        s.songs();
        check("observers have no side effects", s.size() == before);
    }

    // --- Producer ต้องคืนตัวใหม่ ไม่แก้ตัวเดิม ---
    private static void testProducer() {
        System.out.println("\n-- Producer (shuffled) --");

        Playlist original = new Playlist(Arrays.asList("A", "B", "C", "D"));
        Playlist shuffled = original.shuffled();

        check("shuffled has the same size", shuffled.size() == original.size());

        List<String> a = new ArrayList<String>(original.songs());
        List<String> b = new ArrayList<String>(shuffled.songs());
        Collections.sort(a);
        Collections.sort(b);
        check("shuffled contains exactly the same songs", a.equals(b));

        check("shuffled does not mutate the original",
                original.songs().equals(Arrays.asList("A", "B", "C", "D")));

        // mutate ตัวใหม่ต้องไม่กระทบตัวเดิม
        shuffled.add("E");
        check("mutating the result does not affect the original",
                original.size() == 4);

        // boundary: shuffle เพลย์ลิสต์ว่างต้องไม่พัง
        Playlist emptyShuffled = new Playlist().shuffled();
        check("shuffling an empty playlist is safe", emptyShuffled.size() == 0);
    }

    // --- ทดสอบว่าไม่เกิด representation exposure ---
    private static void testExposure() {
        System.out.println("\n-- Representation Exposure --");

        // ขาออก: แก้ list ที่ได้จาก songs() ต้องไม่กระทบ rep
        Playlist s = new Playlist();
        s.add("A");

        List<String> got = s.songs();
        got.clear();
        check("clearing result of songs() does not affect playlist",
                s.size() == 1);

        got = s.songs();
        got.add("injected");
        check("adding to result of songs() does not affect playlist",
                s.size() == 1 && !s.contains("injected"));

        // สองครั้งต้องเป็นคนละ object
        check("songs() returns a fresh list each call",
                s.songs() != s.songs());

        // ขาเข้า: แก้ list ที่ส่งให้ constructor ต้องไม่กระทบ rep
        List<String> input = new ArrayList<String>(Arrays.asList("A", "B"));
        Playlist p = new Playlist(input);

        input.clear();
        check("clearing constructor argument does not affect playlist",
                p.size() == 2);

        input.add("injected");
        check("adding to constructor argument does not affect playlist",
                !p.contains("injected"));
    }
}