package searchengine.dto.serachEngine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLetterDecoderEncoder;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemma {
    private static String unlemmedSent;


    public Lemma(String unlemmedSent) throws IOException {
        Lemma.unlemmedSent = unlemmedSent;
    }

//    public List<String> getLemmaSet() throws IOException {
    public  String[] getLemmaSet() throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        List<String> wordBase = new ArrayList<>();
        EnglishLetterDecoderEncoder englishLetterDecoderEncoder = new EnglishLetterDecoderEncoder();
         List<String> m = new ArrayList<>
                (Arrays.asList(unlemmedSent.replaceAll("[:;,.!?\\\\-]", "")
                        .trim().toLowerCase().split(" ")));

        for (String s : m) {
            if (englishLetterDecoderEncoder.checkString(s)) {
                wordBase.add(s.toLowerCase());
                continue;
            }
            if (!luceneMorph.checkString(s)) {
                continue;
            }
            if (luceneMorph.getMorphInfo(s).toString().contains("СОЮЗ")
                    || luceneMorph.getMorphInfo(s).toString().contains("МЕЖД")
                    || luceneMorph.getMorphInfo(s).toString().contains("ПРЕДЛ")
                    || luceneMorph.getMorphInfo(s).toString().contains("ЧАСТ")) {
            } else {
                wordBase.add(luceneMorph.getNormalForms(s).toString().replaceAll("[\\]\\[]", ""));
            }
        }


        return wordBase.toArray(new String[0]);
    }

    public List<String> collectLemmas() throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();

        EnglishLetterDecoderEncoder englishLetterDecoderEncoder = new EnglishLetterDecoderEncoder();
        String[] m = unlemmedSent.replaceAll("[:;,.!?\\\\-]", "")
                .trim().toLowerCase().split(" ");
        String[] wordBase = new String[m.length];
        List<String> wordBase1 = new ArrayList<>();

        for (int i = 0; i < m.length; i++) {

            if (englishLetterDecoderEncoder.checkString(m[i])) {
                wordBase[i] = m[i].toLowerCase();
                continue;
            }
            if (!luceneMorph.checkString(m[i])) {
                continue;
            }
            if (luceneMorph.getMorphInfo(m[i]).toString().contains("СОЮЗ")
                    || luceneMorph.getMorphInfo(m[i]).toString().contains("МЕЖД")
                    || luceneMorph.getMorphInfo(m[i]).toString().contains("ПРЕДЛ")
                    || luceneMorph.getMorphInfo(m[i]).toString().contains("ЧАСТ")) {
            } else {
                wordBase[i] = luceneMorph.getNormalForms(m[i]).toString().replaceAll("[\\]\\[]", "");
//                  wordBase.add( luceneMorph.getNormalForms(s).toString().replaceAll("[\\]\\[]",""));

            }

        }
        for (int i = 0; i < wordBase.length; i++) {

            if (wordBase[i] != null && !Objects.equals(wordBase[i], "") && !wordBase1.contains(wordBase[i])) {
                wordBase1.add(wordBase[i].trim());
            }
        }
        return wordBase1;
    }


        public String[] collectUniqueLemmas() throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        List<String> wordBase = new ArrayList<>();
        List<String> wordBase1 = new ArrayList<>();
        EnglishLetterDecoderEncoder englishLetterDecoderEncoder = new EnglishLetterDecoderEncoder();
        List<String> m = new ArrayList<>
                (Arrays.asList(unlemmedSent.replaceAll("[:;,.!?\\\\-]", "")
                        .trim().toLowerCase().split(" ")));

        for (String s:m){

            if(englishLetterDecoderEncoder.checkString(s)){
                wordBase.add(s.toLowerCase());
                continue;
            }
            if(!luceneMorph.checkString(s)) {
                continue;
            }
            if(luceneMorph.getMorphInfo(s).toString().contains("СОЮЗ")
                    || luceneMorph.getMorphInfo(s).toString().contains("МЕЖД")
                    || luceneMorph.getMorphInfo(s).toString().contains("ПРЕДЛ")
                    || luceneMorph.getMorphInfo(s).toString().contains("ЧАСТ")){
            }else {
                wordBase.add( luceneMorph.getNormalForms(s).toString().replaceAll("[\\]\\[]",""));

            }

        }
        for(int i=0;i<wordBase.size();i++){
            if(!wordBase.get(i).isEmpty()  && !wordBase1.contains(wordBase.get(i)+" - " + wordBase.stream().filter(wordBase.get(i)::equals).count())){

                wordBase1.add(wordBase.get(i) +" - " + wordBase.stream().filter(wordBase.get(i)::equals).count());
            }
        }
//        ArrayList<String> wordBaseFinal =new ArrayList<>();
//        wordBase1.forEach(w->{
//            if(Integer.parseInt(w.replaceAll("[\\D-]","").trim())==1){
//                wordBaseFinal.add(w);
////                System.out.println(w);
//             }
//        });
        return wordBase1.toArray(new String[0]);
    }
}
