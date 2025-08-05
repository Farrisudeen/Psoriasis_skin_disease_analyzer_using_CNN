package com.example.skindetect.service;

import com.example.skindetect.dto.PredictionResult;
import com.example.skindetect.util.ImagePreprocessor;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.*;

@Service
public class ModelService {

    private SavedModelBundle model;
    private final List<String> classes = Arrays.asList(
            "Acne and Rosacea", "Actinic Keratosis Basal Cell Carcinoma", "Atopic Dermatitis",
            "Bullous Disease", "Cellulitis Impetigo", "Eczema", "Exanthems and Drug Eruptions",
            "Hair Loss and Alopecia", "Monkeypox", "Normal"
    );
    private final Map<String, List<String>> dosMap = new HashMap<>();
    private final Map<String, List<String>> dontsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        model = SavedModelBundle.load("src/main/resources/saved_model", "serve");
        dosMap.put("Actinic Keratosis Basal Cell Carcinoma",
                Collections.singletonList("Focus on a diet rich in antioxidants, such as fruits, vegetables, nuts, and seeds."));
        dontsMap.put("Actinic Keratosis Basal Cell Carcinoma",
                Collections.singletonList("Limit processed foods and red meat."));
        dosMap.put("Atopic Dermatitis",
                Collections.singletonList("Opt for anti-inflammatory foods like fish, leafy greens, and berries."));
        dontsMap.put("Atopic Dermatitis",
                Collections.singletonList("Avoid common triggers like dairy, gluten, and processed foods."));
        dosMap.put("Bullous Disease",
                Collections.singletonList("Consume a well-balanced diet with plenty of protein, vitamins, and minerals."));
        dontsMap.put("Bullous Disease",
                Collections.singletonList("Avoid spicy and acidic foods."));
        dosMap.put("Cellulitis Impetigo", Arrays.asList("Eat a diet rich in vitamin C, zinc, and protein to boost your immune system.", "Stay hydrated."));
        dontsMap.put("Cellulitis Impetigo",
                Collections.singletonList("Limit processed foods."));
        dosMap.put("Eczema",
                Collections.singletonList("Include foods rich in omega-3 fatty acids like salmon and flaxseeds."));
        dontsMap.put("Eczema",
                Collections.singletonList("Avoid common triggers like dairy, eggs, and nuts."));
        dosMap.put("Exanthems and Drug Eruptions",
                Collections.singletonList("Focus on a diet rich in antioxidants and vitamins to support skin health."));
        dontsMap.put("Exanthems and Drug Eruptions",
                Collections.singletonList("Avoid foods that may exacerbate symptoms."));
        dosMap.put("Monkeypox", Arrays.asList("Eat a balanced diet with plenty of fruits, vegetables, lean proteins, and whole grains.", "Stay hydrated."));
        dontsMap.put("Monkeypox",
                Collections.singletonList("Avoid salty foods such as chips, instant packaged foods, ready soups."));
    }

    public PredictionResult predict(Path imagePath) throws Exception {
        try (TFloat32 input = ImagePreprocessor.preprocess(imagePath)) {
            List<TFloat32> outputs = model.session().runner()
                    .feed("serving_default_input_1", input)
                    .fetch("StatefulPartitionedCall").run();
            TFloat32 resultTensor = outputs.get(0);
            float[] scores = new float[classes.size()];
            resultTensor.copyTo(Shape.of(1, classes.size()), (FloatNdArray nda) -> {
                for (int i = 0; i < classes.size(); i++) {
                    scores[i] = nda.getFloat(0, i);
                }
            });
            int idx = 0;
            for (int i = 1; i < scores.length; i++) {
                if (scores[i] > scores[idx]) idx = i;
            }
            String label = classes.get(idx);
            boolean normal = "Normal".equals(label);
            List<String> dos = normal ? Collections.emptyList() : dosMap.getOrDefault(label, Collections.emptyList());
            List<String> donts = normal ? Collections.emptyList() : dontsMap.getOrDefault(label, Collections.emptyList());
            return new PredictionResult(label, dos, donts, normal);
        }
    }
}
