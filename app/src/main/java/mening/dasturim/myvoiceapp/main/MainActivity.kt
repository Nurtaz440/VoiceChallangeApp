package mening.dasturim.myvoiceapp.main

import android.Manifest
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import mening.dasturim.myvoiceapp.R
import mening.dasturim.myvoiceapp.databinding.ActivityMainBinding
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    lateinit var image_uri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        storagePermission =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_image -> {
                showDialog()
            }
            R.id.menu_settings -> {
                Toast.makeText(this, "error keldi", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialog() {
        val names = arrayOf("Camera", "Gallery")

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)

        alertDialog.setTitle("Rasmni tanlang")
        alertDialog.setItems(names, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, postion: Int) {
                if (postion == 0) {
                    if (!checkCameraPermission()) {
                        //if camera not allowed
                        requestCameraPermission()
                    } else {
                        pickCamera()
                    }
                }
                if (postion == 1) {
                    if (!checkStoragePermission()) {
                        //if camera not allowed
                        requestStoragePermission()
                    } else {
                        pickGallery()
                    }

                }
            }

        })
        alertDialog.create().show()
    }

    internal fun pickGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_PERMISSION)
    }

    internal fun pickCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Yangisini ushlash")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Rasmnan textga olib otish")

        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, Constants.PICK_CAMER_PERMISSION)

    }

    internal fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            storagePermission,
            Constants.CHECK_STORAGE_PERMISSION
        )
    }

    internal fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result

    }

    internal fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, Constants.CHECK_CAMERA_PERMISSION)
    }

    internal fun checkCameraPermission(): Boolean {
        val result: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result && result1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            Constants.CHECK_CAMERA_PERMISSION->{
                if (grantResults.size>0){
                    val cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED
                    val writeStorageAccepted= grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera()
                    }else{
                        Toast.makeText(this,"Permission inkor qilindi",Toast.LENGTH_SHORT).show()
                    }

                }

            }
            Constants.CHECK_STORAGE_PERMISSION->{
                if (grantResults.size>0){

                    val writeStorageAccepted= grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED

                    if (writeStorageAccepted){
                        pickGallery()
                    }else{
                        Toast.makeText(this,"Permission inkor qilindi",Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            if (requestCode== Constants.PICK_GALLERY_PERMISSION){
                CropImage.activity(data?.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
            }
            if (requestCode == Constants.PICK_CAMER_PERMISSION){
                CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
           val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK){
                val resultUri:Uri = result.uri

                activityMainBinding.imageTv.setImageURI(resultUri)

                val bitmapDrawable:BitmapDrawable=activityMainBinding.imageTv.drawable as BitmapDrawable
                val bitmap : Bitmap=bitmapDrawable.bitmap

                val recoginizer:TextRecognizer=TextRecognizer.Builder(applicationContext).build()

                if (!recoginizer.isOperational){
                    Toast.makeText(this ,"error recoginizer",Toast.LENGTH_SHORT).show()

                }else{
                    val frame:Frame=Frame.Builder().setBitmap(bitmap).build()
                    val items: SparseArray<TextBlock> =recoginizer.detect(frame)

                    val sb:StringBuilder= StringBuilder()

                    for (i in 0 until items.size() step 1){
                        val myItems : TextBlock=items.valueAt(i)
                        sb.append(myItems.value)
                        sb.append("\n")

                    }
                    activityMainBinding.evNatija.setText(sb.toString())

                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                val error:Exception=result.error

                Toast.makeText(this,"" +error,Toast.LENGTH_SHORT).show()


            }
        }
    }
}