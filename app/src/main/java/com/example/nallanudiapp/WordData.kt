package com.example.nallanudiapp

data class Word(
    val english: String,
    val kannada: String,
    val meaning: String,
    val subject: String
)

val wordDatabase = listOf(

    // ===== SCIENCE =====
    Word("gravity","ಗುರುತ್ವಾಕರ್ಷಣೆ","Force that pulls objects","Science"),
    Word("force","ಬಲ","Push or pull","Science"),
    Word("energy","ಶಕ್ತಿ","Ability to do work","Science"),
    Word("atom","ಅಣು","Smallest unit of matter","Science"),
    Word("cell","ಕೋಶ","Unit of life","Science"),
    Word("light","ಬೆಳಕು","Form of energy","Science"),
    Word("sound","ಧ್ವನಿ","Energy we hear","Science"),
    Word("heat","ಉಷ್ಣ","Energy transfer","Science"),
    Word("temperature","ತಾಪಮಾನ","Measure of heat","Science"),
    Word("mass","ಭಾರ","Amount of matter","Science"),
    Word("velocity","ವೇಗ","Speed with direction","Science"),
    Word("acceleration","ವೇಗವರ್ಧನೆ","Change in velocity","Science"),
    Word("pressure","ಒತ್ತಡ","Force per unit area","Science"),
    Word("electricity","ವಿದ್ಯುತ್","Flow of charge","Science"),
    Word("magnet","ಚುಂಭಕ","Magnetic object","Science"),
    Word("ecosystem","ಪರಿಸರ ವ್ಯವಸ್ಥೆ","Interaction of living things","Science"),
    Word("photosynthesis","ಪ್ರಕಾಶ ಸಂಶ್ಲೇಷಣೆ","Plants make food","Science"),
    Word("respiration","ಉಸಿರಾಟ","Process of breathing","Science"),
    Word("evaporation","ಆವಿಯಾಗುವಿಕೆ","Liquid to gas","Science"),
    Word("condensation","ಘನೀಕರಣ","Gas to liquid","Science"),

    // ===== MATH =====
    Word("addition","ಸೇರಿಸುವಿಕೆ","Sum of numbers","Math"),
    Word("subtraction","ಕಳೆಯುವುದು","Difference","Math"),
    Word("multiplication","ಗುಣಾಕಾರ","Repeated addition","Math"),
    Word("division","ಭಾಗಾಕಾರ","Splitting numbers","Math"),
    Word("percentage","ಶೇಕಡಾವಾರು","Part per hundred","Math"),
    Word("fraction","ಭಾಗಾಕೃತಿ","Part of whole","Math"),
    Word("decimal","ದಶಮಾಂಶ","Fraction form","Math"),
    Word("equation","ಸಮೀಕರಣ","Mathematical statement","Math"),
    Word("algebra","ಬೀಜಗಣಿತ","Study of variables","Math"),
    Word("geometry","ಜ್ಯಾಮಿತಿ","Study of shapes","Math"),
    Word("angle","ಕೋನ","Space between lines","Math"),
    Word("triangle","ತ್ರಿಭುಜ","3-sided shape","Math"),
    Word("square","ಚೌಕ","4 equal sides","Math"),
    Word("rectangle","ಆಯತ","Opposite sides equal","Math"),
    Word("circle","ವೃತ್ತ","Round shape","Math"),

    // ===== COMMERCE =====
    Word("profit","ಲಾಭ","Gain in business","Commerce"),
    Word("loss","ನಷ್ಟ","Loss in business","Commerce"),
    Word("budget","ಬಜೆಟ್","Financial plan","Commerce"),
    Word("market","ಮಾರುಕಟ್ಟೆ","Buying/selling place","Commerce"),
    Word("income","ಆದಾಯ","Money earned","Commerce"),
    Word("expense","ಖರ್ಚು","Money spent","Commerce"),
    Word("capital","ಮೂಲಧನ","Initial investment","Commerce"),
    Word("revenue","ಆದಾಯ","Total income","Commerce"),
    Word("tax","ತೆರಿಗೆ","Government charge","Commerce"),
    Word("bank","ಬ್ಯಾಂಕ್","Financial institution","Commerce"),
    Word("loan","ಸಾಲ","Borrowed money","Commerce"),
    Word("interest","ಬಡ್ಡಿ","Extra money paid","Commerce"),
    Word("credit","ಕ್ರೆಡಿಟ್","Borrowing facility","Commerce"),
    Word("debit","ಡೆಬಿಟ್","Money withdrawn","Commerce"),
    Word("account","ಖಾತೆ","Financial record","Commerce")
)