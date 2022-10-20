package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap

    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 1

    private lateinit var pointOfInterest: PointOfInterest

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        //TODO: add the map setup implementation
        // Obtain the childFragmentManager and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //Default location
        val sydney = LatLng(-34.0, 151.0)
        val zoomLevel = 15f
        map.addMarker(MarkerOptions().position(sydney).title("Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))
        map.uiSettings.isZoomControlsEnabled = true

        enableUserLocation()
        setPoiClickListener(map)
        onLocationSelected()

    }

    private fun setPoiClickListener(map: GoogleMap) {

        map.setOnPoiClickListener { poi ->

            map.clear()
            pointOfInterest = poi

            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )

            map.addCircle(
                CircleOptions()
                    .center(poi.latLng)
                    .radius(200.0)
                    .strokeColor(Color.argb(255,255,0,0))
                    .fillColor(Color.argb(64,255,0,0)).strokeWidth(4F)

            )

            poiMarker.showInfoWindow()


        }
    }


    private fun onLocationSelected() {

        binding.saveButton.setOnClickListener{


            if (this::pointOfInterest.isInitialized){
                _viewModel.latitude.value = pointOfInterest.latLng.latitude
                _viewModel.longitude.value = pointOfInterest.latLng.longitude
                _viewModel.reminderSelectedLocationStr.value = pointOfInterest.name
                _viewModel.selectedPOI.value = pointOfInterest
                _viewModel.navigationCommand.value = NavigationCommand.To(SelectLocationFragmentDirections.toSaveReminder())
            }else{
                Toast.makeText(context, "Please select a location", Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun enableUserLocation() {

        when {
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) -> {

                map.isMyLocationEnabled = true
                Toast.makeText(context, "Location permission is granted.", Toast.LENGTH_LONG).show()
            }
            (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) ->{
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            }

            else ->
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the location

        when (requestCode) {
            FINE_LOCATION_ACCESS_REQUEST_CODE -> {

                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableUserLocation()

                } else {
                    Toast.makeText(context, "Location permission was not granted.", Toast.LENGTH_LONG).show()
                }

            }

        }

    }


}



