package com.master.iot.luzi.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.master.iot.luzi.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    lateinit var preferences: SharedPreferences
    lateinit var ccaaPreference: ListPreference
    lateinit var provincesPreference: ListPreference
    lateinit var municipalitiesPreference: ListPreference
    lateinit var productsPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        initPreferences()
        setUpListeners()
        setUpObservers()
        renderPetrolListPreferences()
    }

    override fun onStop() {
        super.onStop()
        settingsViewModel.clearDisposables()
    }

    private fun initPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        ccaaPreference = findPreference<ListPreference>(PREFERENCES_PETROL_CCAA) as ListPreference
        provincesPreference =
            findPreference<ListPreference>(PREFERENCES_PETROL_PROVINCE) as ListPreference
        municipalitiesPreference =
            findPreference<ListPreference>(PREFERENCES_PETROL_MUNICIPALITY) as ListPreference
        productsPreference =
            findPreference<ListPreference>(PREFERENCES_PETROL_PRODUCT_TYPE) as ListPreference
    }

    private fun setUpListeners() {
        ccaaPreference.setOnPreferenceChangeListener { preference, newValue ->
            ccaaPreference.value = newValue.toString()
            preference.summary = ccaaPreference.entry
            settingsViewModel.getProvincePreferences(idCCAA = newValue.toString())
            settingsViewModel.getMunicipalityPreferences(idProvince = newValue.toString())
            false
        }
        provincesPreference.setOnPreferenceChangeListener { preference, newValue ->
            provincesPreference.value = newValue.toString()
            preference.summary = provincesPreference.entry
            settingsViewModel.getMunicipalityPreferences(idProvince = newValue.toString())
            false
        }
        municipalitiesPreference.setOnPreferenceChangeListener { preference, newValue ->
            municipalitiesPreference.value = newValue.toString()
            preference.summary = municipalitiesPreference.entry
            false
        }
        productsPreference.setOnPreferenceChangeListener { preference, newValue ->
            productsPreference.value = newValue.toString()
            preference.summary = productsPreference.entry
            false
        }
    }

    private fun setUpObservers() {
        settingsViewModel.ccaaData.observe(viewLifecycleOwner) { list ->
            ccaaPreference.apply {
                entries = list.map { it.ccaa }.toTypedArray()
                entryValues = list.map { it.idCcaa }.toTypedArray()
                summary = ccaaPreference.entry
            }
        }
        settingsViewModel.provincesData.observe(viewLifecycleOwner) { list ->
            provincesPreference.apply {
                entries = list.map { it.province }.toTypedArray()
                entryValues = list.map { it.idProvince }.toTypedArray()
                summary = provincesPreference.entry
            }
        }
        settingsViewModel.municipalitiesData.observe(viewLifecycleOwner) { list ->
            municipalitiesPreference.apply {
                entries = list.map { it.municipality }.toTypedArray()
                entryValues = list.map { it.idMunicipality }.toTypedArray()
                summary = municipalitiesPreference.entry
            }
        }
        settingsViewModel.productsData.observe(viewLifecycleOwner) { list ->
            productsPreference.apply {
                entries = list.map { it.name }.toTypedArray()
                entryValues = list.map { it.id }.toTypedArray()
                summary = productsPreference.entry
            }
        }
    }

    private fun renderPetrolListPreferences() {
        val idCcaa =
            preferences.getString(PREFERENCES_PETROL_CCAA, PREFERENCES_PETROL_ID_CCAA_DEFAULT)
                .toString()
        val idProvince = preferences.getString(
            PREFERENCES_PETROL_PROVINCE,
            PREFERENCES_PETROL_ID_PROVINCE_DEFAULT
        ).toString()
        settingsViewModel.getCCAAPreferences()
        settingsViewModel.getProvincePreferences(idCCAA = idCcaa)
        settingsViewModel.getMunicipalityPreferences(idProvince = idProvince)
        settingsViewModel.getProductsPreferences()
    }

}