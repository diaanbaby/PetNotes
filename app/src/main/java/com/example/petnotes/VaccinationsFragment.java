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

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import classes.Pet;
import classes.PetDBHelper;
import classes.Vaccination;
import classes.VaccinationListAdapter;
import classes.VetVisit;
import classes.VetVisitListAdapter;

public class VaccinationsFragment extends Fragment {

    private static final String TAG = "VaccinationsFragment";
    private ListView listViewVaccinations;
    private ArrayList<Vaccination> vaccinations;
    private PetDBHelper dbHelper;
    private Pet pet;

    VaccinationListAdapter adapter;

    public VaccinationsFragment(PetDBHelper dbHelper, Pet pet) {
        this.dbHelper = dbHelper;
        this.pet = pet;
    }

    public VaccinationsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vaccinations = dbHelper.getVaccinations(pet.getId());
        Log.d(TAG, "Retrieved " + vaccinations.size() + " vaccinations from the database");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccinations, container, false);
        listViewVaccinations = view.findViewById(R.id.recyclerView_vaccinations);
        Collections.reverse(vaccinations);

        listViewVaccinations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Vaccination vaccination = vaccinations.get(position);
                showDeleteConfirmationDialog(vaccination);
                return true;
            }
        });

        adapter = new VaccinationListAdapter(getActivity(), vaccinations);
        listViewVaccinations.setAdapter(adapter);
        return view;
    }

    public void UpdateVaccinationList()
    {
        vaccinations.clear();
        vaccinations.addAll(dbHelper.getVaccinations(pet.getId()));
        Collections.reverse(vaccinations);
        ((VaccinationListAdapter) listViewVaccinations.getAdapter()).notifyDataSetChanged();
    }

    public static VaccinationsFragment newInstance(PetDBHelper dbHelper, Pet pet) {
        return new VaccinationsFragment(dbHelper, pet);
    }

    private void showDeleteConfirmationDialog(final Vaccination vaccination) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_this_visit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteVaccination(vaccination);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    private void deleteVaccination(Vaccination vaccination) {
        dbHelper.deleteVetVisit(pet.getId(), vaccination.getName());
        updateVetVisitsList();
    }

    private void updateVetVisitsList() {
        vaccinations.clear();
        vaccinations.addAll(dbHelper.getVaccinations(pet.getId()));
        adapter.notifyDataSetChanged();
    }
}
