package dao;

import config.DBConnection;
import model.Personel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonelDAO {

    public List<Personel> getAll() {
        List<Personel> list = new ArrayList<>();
        String sql = "SELECT * FROM personel";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Personel(
                        rs.getInt("id"),
                        rs.getString("ad"),
                        rs.getString("soyad"),
                        rs.getString("pozisyon"),
                        rs.getBigDecimal("maas"),
                        rs.getDate("giris_tarihi").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            System.err.println("Personel listesi alınırken hata oluştu!");
            e.printStackTrace();
        }
        return list;
    }

    public Personel getById(int id) {
        String sql = "SELECT * FROM personel WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new Personel(
                        rs.getInt("id"),
                        rs.getString("ad"),
                        rs.getString("soyad"),
                        rs.getString("pozisyon"),
                        rs.getBigDecimal("maas"),
                        rs.getDate("giris_tarihi").toLocalDate()
                );
            }

        } catch (SQLException e) {
            System.err.println("Personel bulunamadı! ID: " + id);
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Personel personel) {
        String sql = "INSERT INTO personel (ad, soyad, pozisyon, maas, giris_tarihi) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, personel.getAd());
            pst.setString(2, personel.getSoyad());
            pst.setString(3, personel.getPozisyon());
            pst.setBigDecimal(4, personel.getMaas());
            pst.setDate(5, Date.valueOf(personel.getGirisTarihi()));

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Personel eklenirken hata oluştu!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Personel personel) {
        String sql = "UPDATE personel SET ad = ?, soyad = ?, pozisyon = ?, maas = ?, giris_tarihi = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, personel.getAd());
            pst.setString(2, personel.getSoyad());
            pst.setString(3, personel.getPozisyon());
            pst.setBigDecimal(4, personel.getMaas());
            pst.setDate(5, Date.valueOf(personel.getGirisTarihi()));
            pst.setInt(6, personel.getId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Personel güncellenirken hata oluştu!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM personel WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Personel silinirken hata oluştu!");
            e.printStackTrace();
            return false;
        }
    }
}