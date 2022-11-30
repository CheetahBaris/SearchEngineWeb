package searchengine.dto.serachEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import searchengine.model.entities.LemmaEntity;
import searchengine.model.entities.IndexEntity;
import searchengine.model.entities.PageEntity;
import org.jsoup.Jsoup;
import searchengine.model.entities.SiteEntity;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class Searcher {
    private List<String> inputLemmas;
    private static final float PERCENT_OF_RARITY = 1.5F;
    private static final double PERCENT_FOR_SNIPPET = 0.005;
    private final List<LemmaEntity> lemmaEntity;
    private final List<PageEntity> pageEntity;
    private final List<IndexEntity> indexEntity;
    private final List<SiteEntity> siteEntity;
    private final List<Float> absRelevant = new ArrayList<>();
    private final List<Integer> absRelevantSingle = new ArrayList<>();
    private final ArrayList<String> err = new ArrayList<>();


    public Searcher(PageService pageService, LemmaService lemmaService, IndexService indexService,
                    SiteService siteService) {
        this.pageEntity = pageService.getAllPages();
        this.lemmaEntity = lemmaService.getAllLemmas();
        this.indexEntity = indexService.getAllIndexes();
        this.siteEntity = siteService.getAllSites();
    }


    public String[] sorterOfRelevant() throws IOException {
        List<String> result = inputChahing();
        List<String> sortedPages = Arrays.stream(getPageRelevant(result).toString().split("\n")).toList();
        String[] data = new String[sortedPages.size()];


        sortedPages = sortedPages.stream().sorted(Comparator.comparingDouble(o ->
                        Double.parseDouble(o.replaceAll(".+(\\|\\|)", ""))))
                .collect(Collectors.toList());


        for (int i = 0; i < sortedPages.size(); i++) {
            data[sortedPages.size() - 1 - i] = sortedPages.get(i);

        }

        return err.isEmpty() ? data : err.toArray(new String[0]);
    }


    private List<String> inputChahing() {
        List<String> result;
        List<String> validLemmas = new ArrayList<>();
        List<String> onlyOftenLemmas = new ArrayList<>();
        for (int i = 0; i < lemmaEntity.size(); i++) {
            for (String s : inputLemmas) {
                if (lemmaEntity.get(i).getLemma().equals(s)) {

                    if (lemmaEntity.get(i).getFrequency() >= pageEntity.get(pageEntity.size() - 1)
                            .getId() / PERCENT_OF_RARITY) {
                        onlyOftenLemmas.add(lemmaEntity.get(i).getId() + lemmaEntity.get(i).getLemma()
                                + lemmaEntity.get(i).getFrequency());
                        continue;
                    }


                    validLemmas.add(lemmaEntity.get(i).getId() + lemmaEntity.get(i).getLemma()
                            + lemmaEntity.get(i).getFrequency());
                }

            }
        }

        if (!validLemmas.isEmpty()) {

            result = validLemmas.stream().sorted(Comparator.comparing(o ->
                    Integer.parseInt(o.replaceFirst("[0-9]+", "")
                            .replaceAll("\\D", "")))).collect(Collectors.toList());

        } else {

            result = onlyOftenLemmas.stream().sorted(Comparator.comparing(o ->
                    Integer.parseInt(o.replaceFirst("[0-9]+", "")
                            .replaceAll("\\D", "")))).collect(Collectors.toList());
        }
        System.out.println(result);
        return result;
    }


    private StringBuilder getPageRelevant(List<String> result) throws IOException {

        List<Integer> pageIdList = new ArrayList<>();
        List<Integer> finalPageIdList = new ArrayList<>();
        List<Integer> lemmId = new ArrayList<>();
        StringBuilder relevantLemmas = new StringBuilder();
        StringBuilder relevantTable = new StringBuilder();
        StringBuilder inputSentence = new StringBuilder();

//                inputLemmas.toString()
//                .replaceAll("[\\[\\],]", "").trim();
        float absRelev = 0;

        for (int i = 0; i < result.size(); i++) {
            String resultClean = result.get(i).replaceAll("\\d", "");
            System.out.println(resultClean +" == "+ resultClean.length());
            if (resultClean.length() > 4) {
                inputSentence.append(resultClean, 0, resultClean.length() - 2)
                        .append(i == result.size() - 1 ? "" : " ");

            } else {
                inputSentence.append(resultClean).append(i == result.size() - 1 ? "" : " ");

            }
        }


//        System.out.println(inputSentence);
        for (int i = 0; i < result.size(); i++) {
            lemmId.add(Integer.parseInt(result.get(i).replaceAll("[^0-9]+[\\D][0-9]+", "")));
        }


        for (int i = 0; i < indexEntity.size(); i++) {
            if (indexEntity.get(i).getLemma_id() == lemmId.get(0)) {
                pageIdList.add(indexEntity.get(i).getPage_id());
            }
        }
//        System.out.println(pageIdList);

        for (int i = 0; i < lemmId.size(); i++) {

            if (!pageIdList.isEmpty()) {
                for (int j = 0; j < indexEntity.size(); j++) {

                    for (int k = 0; k < pageIdList.size(); k++) {

                        if (pageIdList.get(k) == indexEntity.get(j).getPage_id()) {

                            if (indexEntity.get(j).getLemma_id() == lemmId.get(i) && !finalPageIdList
                                    .contains(indexEntity.get(j).getPage_id())) {

                                finalPageIdList.add(indexEntity.get(j).getPage_id());
                            }
                        }
                    }
                }
            }
        }

//        System.out.println(finalPageIdList);
        if (finalPageIdList.isEmpty()) {
            err.add("Список пуст");
        } else {

            for (int i = 0; i < indexEntity.size(); i++) {

                if (lemmId.size() >= 1) {

                    for (int j = 0; j < lemmId.size(); j++) {

                        if (indexEntity.get(i).getLemma_id() == lemmId.get(j)) {

                            absRelev += indexEntity.get(i).getRank();

                            if (j == lemmId.size() - 1 && lemmId.size() > 1) {

                                absRelevant.add(absRelev);
                                absRelev = 0;
                            } else if (j == 0) {

                                for (PageEntity p : pageEntity) {
                                    String snippet1 = "";

                                    if (p.getId() == indexEntity.get(i).getPage_id()) {

                                        String[] listStrings = Jsoup.parse(p.getContent()).text().split("\n");
                                        String htmlOfPage = Jsoup.parse(p.getContent()
                                                .replace("@@@", "'")).html();

                                        for (int l = 0; l < listStrings.length; l++) {

                                            String lowerCasedText = listStrings[l].toLowerCase();
                                            String[] words = lowerCasedText
                                                    .replaceAll("[^\\s[а-я][a-z]]", "").trim()
                                                    .replaceAll("\\t", "")
                                                    .split("\\s");
                                            for (int k = 0; k < words.length - 1; k++) {

                                                StringBuilder matcher = new StringBuilder();

                                                StringBuilder matcherForSnippet = new StringBuilder();

                                                if (words[k].length() >= result.get(0)
                                                        .replaceAll("\\d", "").length()) {


                                                    if (k == words.length - result.size()) {

                                                        for (int x = result.size() - 1; x >= 0; x--) {

                                                            if (words[k - x].length() >= result.get(x)
                                                                    .replaceAll("\\d", "").length()) {

                                                                matcher.append(words[k - x], 0,
                                                                        words[k - x].length() - 2).append(" ");
                                                                matcherForSnippet.append(words[k - x]).append(" ");
                                                            }
                                                        }
                                                    } else {

                                                        for (int x = 0; x < result.size(); x++) {

                                                            if (words[k + x].length() >= result.get(x)
                                                                    .replaceAll("\\d", "").length()) {

                                                                matcher.append(words[k + x], 0, words[k + x]
                                                                        .length() - 2).append(" ");
                                                                matcherForSnippet.append(words[k + x]).append(" ");

                                                            }
                                                        }
                                                    }
                                                    if (matcher.toString().contains(inputSentence)) {


                                                        String newHtmlOfPage = Jsoup.parse(htmlOfPage.toLowerCase())
                                                                .text();

                                                        int beginInd = newHtmlOfPage.toLowerCase().indexOf(
                                                                matcherForSnippet.toString());
                                                        int finalInd = beginInd + matcherForSnippet.toString().length();

                                                        double newHtmlPageLength = newHtmlOfPage.length();
                                                        double newHtmlSnippetLength = newHtmlPageLength
                                                                * PERCENT_FOR_SNIPPET;

                                                        if (beginInd >= newHtmlSnippetLength && finalInd
                                                                <= newHtmlPageLength - newHtmlSnippetLength) {

                                                            snippet1 = newHtmlOfPage.substring(
                                                                    (int) (beginInd - newHtmlSnippetLength),
                                                                    (int) (finalInd + newHtmlSnippetLength));
                                                        } else if (beginInd >= newHtmlSnippetLength
                                                                && finalInd > newHtmlPageLength
                                                                - newHtmlSnippetLength) {

                                                            snippet1 = newHtmlOfPage.substring((int)
                                                                    (beginInd - newHtmlSnippetLength), finalInd);
                                                        } else {

                                                            snippet1 = newHtmlOfPage.substring(0,
                                                                    (int) (finalInd + newHtmlSnippetLength));
                                                        }
                                                         snippet1 = snippet1.replace(matcherForSnippet.toString(),
                                                                "<b>"+matcherForSnippet.toString().trim()
                                                                        +"</b> ");
                                                    }
                                                }
                                            }
                                            if (lemmId.size() == 1) {
                                                absRelevantSingle.add(indexEntity.get(i).getRank());

                                                relevantLemmas.append(siteEntity.get((int) (p.getSite_id().getId()-1)).getUrl())
                                                        .append(" | ")
                                                        .append(siteEntity.get((int) (p.getSite_id().getId()-1)).getName()).append(" | ")
                                                        .append(p.getPath()).append(" | ")
                                                        .append(Jsoup.parse(p.getContent()).title()).append(" | ")
                                                        .append("...").append(snippet1.replace("|", " ")
                                                                .trim())
                                                        .append("...").append("&");
                                            } else {
                                                relevantLemmas.append(siteEntity.get((int) (p.getSite_id().getId()-1)).getUrl())
                                                        .append(" | ")
                                                        .append(siteEntity.get((int) (p.getSite_id().getId()-1)).getName()).append(" | ")
                                                        .append(p.getPath()).append(" | ")
                                                        .append(Jsoup.parse(p.getContent()).title()).append(" | ")
                                                        .append("...").append(snippet1.replace("|", " ")
                                                                .trim())
                                                        .append("...").append("&");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Что-то пошло не так(");
                }
            }
        }
        if (absRelevant.stream().max(Float::compareTo).isEmpty() &&
                absRelevantSingle.stream().max(Integer::compareTo).isEmpty()) {
            err.add("Неверный завпрос!");
            System.out.println("absRelevants - are not present(");
            Thread.currentThread().interrupt();
        } else {

            String[] pairs = relevantLemmas.toString().split("&");

            for (int j = 0; j < pairs.length; j++) {

                if (lemmId.size() == 1) {

                    relevantTable.append(pairs[j]).append(" || ").append(absRelevantSingle.get(j) /
                            absRelevantSingle.stream().max(Integer::compareTo).get()).append("\n");
                } else {

                    relevantTable.append(pairs[j]).append(" || ").append(absRelevant.get(j) /
                            absRelevant.stream().max(Float::compareTo).get()).append("\n");
                }
            }
        }

        return relevantTable;
    }

    public void setInputLemmas(List<String> lems) {

        this.inputLemmas = lems;
    }
}
