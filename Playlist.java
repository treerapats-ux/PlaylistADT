import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Playlist — ADT แทนรายการเพลงที่ผู้ใช้จัดลำดับไว้
 *
 * ค่านามธรรม (A): ลำดับของเพลง เช่น [เพลงA, เพลงB, เพลงC]
 *
 * ตัวอย่างการใช้งาน:
 *     Playlist p = new Playlist();
 *     p.add("Bohemian Rhapsody");
 *     p.add("Imagine");
 *     System.out.println(p.size());   // 2
 */
public class Playlist {

    public static final int MAX_SONGS = 100;

    // ===== representation =====
    private final List<String> songs;

    // TODO 1: เขียน Abstraction Function ตรงนี้
    // Abstraction Function:
    //   AF(songs) = ...

    // TODO 2: เขียน Representation Invariant ตรงนี้ (4 ข้อ)
    // Representation Invariant:
    // 1. songs != null
    // 2. เพลงใน songs ต้องไม่เป็น null
    // 3. ชื่อเพลงใน songs ต้องไม่เป็นสตริงว่าง ("")
    // 4. ชื่อเพลงห้ามซ้ำกัน
    // 5. songs.size() <= MAX_SONGS
        

    // TODO 3: เขียน Safety from rep exposure ตรงนี้
    // Safety from rep exposure:
    //  ตัวแปร songs ถูกประกาศเป็น private final
    // ใน Contructor คัดลอกข้อมูลจาก input list เข้ามาใหม่ (Defensive Copy)
    // ใน Observer song() คัดลอก List ใหม่ส่งกลับไป เพื่อป้องกัน client แก้ข้อมูลภายในตรงๆ

    /**
     * TODO 4: เขียน checkRep()
     * แปลง RI ทุกข้อเป็น assert หนึ่งบรรทัด พร้อมข้อความอธิบาย
     */
    private void checkRep() {
        assert songs != null : "songs is not null";
        assert songs.size() <= MAX_SONGS ;
        Set<String> seen = new HashSet<>();
        for(String s : songs){
            assert s != null ;
            assert s != "";
            assert seen.add(s) ;
        }
        // เขียนโค้ดตรงนี้
    }

    // ===== Creator =====

    /**
     * สร้างเพลย์ลิสต์ว่าง
     */
    public Playlist() {
        this.songs = new ArrayList<>();
        checkRep();
    }

    /**
     * TODO 5: Creator ตัวที่สอง
     * สร้างเพลย์ลิสต์จากรายชื่อเพลงที่ให้มา
     *
     * ระวัง: ห้ามเก็บ reference ของ initial ตรง ๆ (rep exposure!)
     *
     * @param initial รายชื่อเพลงเริ่มต้น ต้องไม่ซ้ำและไม่เกิน MAX_SONGS
     * @throws IllegalArgumentException ถ้า initial ผิดเงื่อนไข
     */
    public Playlist(List<String> initial) {
        if(initial == null) throw new IllegalArgumentException();
        if(initial.size()>MAX_SONGS) throw new IllegalArgumentException();
        Set<String> seen = new HashSet<>();
        for (String s : initial){
            if(s==null) throw new IllegalArgumentException();
            if(s=="") throw new IllegalArgumentException();
            if(!seen.add(s)) throw new IllegalArgumentException();
        }

        this.songs = new ArrayList<>(initial);   // แก้บรรทัดนี้
        checkRep();
        // เขียนโค้ดตรงนี้
    }

    // ===== Mutators =====

    /**
     * TODO 6: เพิ่มเพลงต่อท้ายเพลย์ลิสต์
     *
     * @param song ชื่อเพลง ต้องไม่เป็น null และไม่เป็นสตริงว่าง
     * @return true ถ้าเพิ่มสำเร็จ, false ถ้ามีเพลงนี้อยู่แล้วหรือเต็มแล้ว
     * @throws IllegalArgumentException ถ้า song เป็น null หรือสตริงว่าง
     */
    public boolean add(String song) {

        if(song == null || song == "") throw new IllegalArgumentException();
        if(songs.contains(song)) return false;
        if(songs.size()==MAX_SONGS) return false;
        songs.add(song) ;
        return true;   // แก้บรรทัดนี้
    }

    /**
     * TODO 7: ลบเพลงออกจากเพลย์ลิสต์
     *
     * @param song ชื่อเพลงที่ต้องการลบ
     * @return true ถ้าลบสำเร็จ, false ถ้าไม่พบเพลงนี้
     */
    public boolean remove(String song) {
        if(!songs.contains(song)) return false;
        songs.remove(song);
        return true;   // แก้บรรทัดนี้
    }

    // ===== Observers =====

    /**
     * TODO 8: คืนจำนวนเพลงในเพลย์ลิสต์
     */
    public int size() {
        
        return songs.size();   // แก้บรรทัดนี้
    }

    /**
     * TODO 9: ตรวจว่ามีเพลงนี้อยู่หรือไม่
     */
    public boolean contains(String song) {

        return songs.contains(song);   // แก้บรรทัดนี้
    }

    /**
     * TODO 10: คืนรายชื่อเพลงทั้งหมดตามลำดับ
     *
     * ระวัง: ห้ามคืน reference ของ songs ตรง ๆ (rep exposure!)
     */
    public List<String> songs() {
        return new ArrayList<>(songs);   // แก้บรรทัดนี้
    }

    // ===== Producer =====

    /**
     * TODO 11: คืนเพลย์ลิสต์ใหม่ที่มีเพลงเดียวกันแต่สลับลำดับ
     *
     * ระวัง: ห้ามแก้เพลย์ลิสต์เดิม (this) เด็ดขาด
     *
     * @return เพลย์ลิสต์ใหม่ที่สลับลำดับแล้ว
     */
    public Playlist shuffled() {
        List<String> copy = new ArrayList<>(songs);
        Collections.shuffle(copy);
        return new Playlist(copy);   // แก้บรรทัดนี้
    }

    @Override
    public String toString() {
        return songs.toString();
    }
}
