package com.example.smartlokerservicejavabetaver;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlokerservicejavabetaver.lokerlocation.LokerLocationAdapter;
import com.example.smartlokerservicejavabetaver.lokerlocation.LokerLocationData;
import com.example.smartlokerservicejavabetaver.lokerlocation.LokerLocationModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    RecyclerView rvLokerLocation;
    SearchView searchView;
    LokerLocationAdapter adapter;
    ArrayList<LokerLocationModel> lokasiLoker;

    public Home(ArrayList<LokerLocationModel> lokasiLoker) {
        this.lokasiLoker = lokasiLoker;
    }

    public Home(int contentLayoutId, ArrayList<LokerLocationModel> lokasiLoker) {
        super(contentLayoutId);
        this.lokasiLoker = lokasiLoker;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        lokasiLoker = new ArrayList<>();

        rvLokerLocation = v.findViewById(R.id.rv_loker_location);
        searchView = v.findViewById(R.id.search_view);
        lokasiLoker.addAll(LokerLocationData.getLokerLocation());
        adapter = new LokerLocationAdapter(lokasiLoker);
        rvLokerLocation.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        rvLokerLocation.setAdapter(adapter);

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });


        adapter.setOnItemClickListener(new LokerLocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(String name) {
                makeText(getContext(),name, LENGTH_SHORT).show();
                Intent i = new Intent(Home.this.getContext(), LokerActivity.class);
                i.putExtra("Lokasi", name);
                startActivity(i);

            }
        });

        return v;
    }

    private void filterList(String text) {
        ArrayList<LokerLocationModel> filteredList = new ArrayList<>();
        for (LokerLocationModel item : lokasiLoker){
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){
//            makeText(this, "Not Found", LENGTH_SHORT).show();
        }else {
            adapter.setFilteredList(filteredList);
        }
    }
}