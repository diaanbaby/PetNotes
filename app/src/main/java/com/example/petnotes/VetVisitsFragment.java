package com.example.petnotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.petnotes.databinding.FragmentVetVisitsBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import classes.Pet;
import classes.PetDBHelper;
import classes.Vaccination;
import classes.VaccinationListAdapter;
import classes.VetVisit;
import classes.VetVisitListAdapter;

public class VetVisitsFragment extends Fragment {

    private static final String TAG = "VetVisitsFragment";
    private ListView listViewVaccinations;
    private ArrayList<VetVisit> vetVisits;
    private PetDBHelper dbHelper;
    private Pet pet;
    VetVisitListAdapter adapter;
    FragmentVetVisitsBinding binding;

    public VetVisitsFragment(PetDBHelper dbHelper, Pet pet) {
        this.dbHelper = dbHelper;
        this.pet = pet;
    }

    public VetVisitsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentVetVisitsBinding.inflate(getLayoutInflater());
        vetVisits = dbHelper.getVetVisits(pet.getId());
        Log.d(TAG, "Retrieved " + vetVisits.size() + " vaccinations from the database");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccinations, container, false);
        listViewVaccinations = view.findViewById(R.id.recyclerView_vaccinations);

        Collections.reverse(vetVisits);

        listViewVaccinations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                VetVisit vetVisit = vetVisits.get(position);
                showDeleteConfirmationDialog(vetVisit);
                return true;
            }
        });

        adapter = new VetVisitListAdapter(getActivity(), vetVisits);
        listViewVaccinations.setAdapter(adapter);
        return view;
    }

    public void UpdateVetVisitsList()
    {
        vetVisits.clear();
        vetVisits.addAll(dbHelper.getVetVisits(pet.getId()));
        Collections.reverse(vetVisits);
        ((VetVisitListAdapter) listViewVaccinations.getAdapter()).notifyDataSetChanged();
    }

    public static VetVisitsFragment newInstance(PetDBHelper dbHelper, Pet pet) {
        return new VetVisitsFragment(dbHelper, pet);
    }

    private void showDeleteConfirmationDialog(final VetVisit vetVisit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_this_visit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteVetVisit(vetVisit);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    private void deleteVetVisit(VetVisit vetVisit) {
        dbHelper.deleteVetVisit(pet.getId(), vetVisit.getName());
        updateVetVisitsList();
    }

    private void updateVetVisitsList() {
        vetVisits.clear();
        vetVisits.addAll(dbHelper.getVetVisits(pet.getId()));
        adapter.notifyDataSetChanged();
    }

}
