package com.example.smartlokerservicejavabetaver.lokerlocation;

import java.util.ArrayList;

public class LokerLocationData {

    public static ArrayList<LokerLocationModel> getLokerLocation(){
        ArrayList<LokerLocationModel> lokasiLoker = new ArrayList<>();

        lokasiLoker.add(new LokerLocationModel("Depok", "Depok", "Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Jakarta", "Jakarta","Tidak Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Cibinong", "Cibinong","Tidak Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Bojong Gede", "Bojonggede","Tidak Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Bekasi", "Bekasi","Tidak Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Tanggerang", "Tanggerang","Tidak Tersedia"));
        lokasiLoker.add(new LokerLocationModel("Bogor", "Bogor","Tidak Tersedia"));

        return lokasiLoker;
    }
}
