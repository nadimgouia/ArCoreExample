package com.example.arcoreexample

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    lateinit var catRenderable:ModelRenderable
    lateinit var arFragment: ArFragment
    lateinit var name_animal: ViewRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.scene_form_fragment) as ArFragment
        // hiding the plane discovery
        // arFragment.getPlaneDiscoveryController().hide();
        // arFragment.getPlaneDiscoveryController().setInstructionView(null);

        setUpModel()

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            createModel(anchorNode)
        }



    }

    private fun createModel(anchorNode: AnchorNode) {
        val cat = TransformableNode(arFragment.transformationSystem)
        // set scale min and max
        cat.apply {
            scaleController.minScale = 2f
            scaleController.maxScale = 4f
            setParent(anchorNode)
            renderable = catRenderable
            select()
        }

        addModelName(anchorNode, cat, "CAT")
    }

    private fun addModelName(anchorNode: AnchorNode, model: TransformableNode, name: String) {
        val nameView = TransformableNode(arFragment.transformationSystem)
        nameView.apply {
            localPosition = Vector3(0f, model.localPosition.y+0.5f, 0f)
            setParent(anchorNode)
            renderable = name_animal
            select()
        }

        var tvAnimalName:TextView = name_animal.view as TextView
        tvAnimalName.text = name
        tvAnimalName.setOnClickListener {
            anchorNode.setParent(null)
        }

    }

    private fun setUpModel() {

        ViewRenderable.builder()
            .setView(this, R.layout.name_animal)
            .build()
            .thenAccept {renderable -> name_animal = renderable}

        ModelRenderable.builder()
            .setSource(this, R.raw.cat)
            .build()
            .thenAccept {modelRenderable: ModelRenderable -> catRenderable = modelRenderable
                // Write an OBJ file
                val objOutputStream: OutputStream =
                    FileOutputStream(filesDir.absoluteFile.toString() + "/simpleSample_written.obj")
                //   ObjWriter.write(modelRenderable, objOutputStream)
            }
            .exceptionally {
                Toast.makeText(this,"Unable to load Bear model", Toast.LENGTH_LONG).show()
                null
            }
    }
}
