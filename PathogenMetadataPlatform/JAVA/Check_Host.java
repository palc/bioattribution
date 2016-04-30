package biosampleparser;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author WCHANG
 */
public class Check_Host {

    // The following is a list of strings determined to represent a 
    // human host.  There may be others.
    private static List<String> myHumanList
            = Arrays.asList("Homo sapiens",
                            "human",
                            "Human",
                            "Hospitalized patient",
                            "Non-hospitalized person",
                            "healthy un-inoculated individuals",
                            "Human B-Cells",
                            "Human, male, child",
                            "Human gut metagenome",
                            "homo sapiens, soil",
                            "Healthy Human Human infant",
                            "Human, male, child",
                            "Homo sapiens (phaeohyphomycosis of the brain)",
                            "Human-Oral",
                            "Human sapiens",
                            "homo sapiens",
                            "Human milk metagenome",
                            "Environment (human-associated)",
                            "human anorexic",
                            "human (infant)",
                            "Homo Sapiens",
                            "Human Healthy Control",
                            "Homo sapiens sapiens",
                            "Human blood, renal transplant",
                            "Human being",
                            "H.sapiens",
                            "human metagenome",
                            "Homo sapiense",
                            "Water, humans",
                            "Homo_sapiens",
                            "Homo sapiens (brain abscess)",
                            "human sputum",
                            "Homo sapiens (Human)",
                            "Human skin",
                            "Human Patient",
                            "human scalp",
                            "Human Endothelial Cells",
                            "human obese",
                            "Homo sapiens, freshwater",
                            "\"Homo sapiens\"",
                            "SMC_human",
                            "Homo sapiens, infants",
                            "human-oral");
    
    public static boolean isHuman(String specificHost) {
        
        return myHumanList.contains(specificHost);
    }
}
