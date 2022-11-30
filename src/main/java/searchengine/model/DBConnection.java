package searchengine.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.index.DataForIndexing;
import searchengine.services.*;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DBConnection {
    private static Connection connection;

    private static String dbName = "search_engine";
    private static String dbUser = "root";
    private static String dbPass = "Freerauner12";
//    private static StringBuffer insertPage = new StringBuffer();
//    private static StringBuffer insertField = new StringBuffer();
//    private static StringBuffer insertLemma = new StringBuffer();
//    private static StringBuffer insertIndex = new StringBuffer();
//    private static StringBuffer insertErrPage = new StringBuffer();
//    private static StringBuffer insertSite = new StringBuffer();
    private static DataForIndexing dataForIndexing;
    private static final AtomicInteger pageCount = new AtomicInteger();
    private static PageService pageService;
    private static FieldService fieldService;
    private static LemmaService lemmaService;
    private static IndexService indexService;


    @Autowired
    public DBConnection(DataForIndexing dataForIndexing, PageService pageService
            , FieldService fieldService, LemmaService lemmaService, IndexService indexService) {
        DBConnection.pageService = pageService;
        DBConnection.fieldService = fieldService;
        DBConnection.lemmaService = lemmaService;
        DBConnection.dataForIndexing = dataForIndexing;
        DBConnection.indexService = indexService;
    }


    public static Connection getConnection() {

        if (connection == null && !dataForIndexing.isIndexPage()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                        + dbName + "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS page;");
//                connection.createStatement().execute("DROP TABLE IF EXISTS field;");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma;");
                connection.createStatement().execute("DROP TABLE IF EXISTS `index`;");
                connection.createStatement().execute("DROP TABLE IF EXISTS site;");

                connection.createStatement().execute("CREATE TABLE site(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "status ENUM('INDEXING','INDEXED','FAILED') NOT NULL , " +
                        "status_time DATETIME  NOT NULL, " +
                        "last_error TEXT," +
                        "url VARCHAR(255) NOT NULL, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "PRIMARY KEY(id))");

                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "site_id INT NOT NULL," +
                        "path  TEXT NOT NULL , " +
                        "code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL, " +
                        "PRIMARY KEY(id)," +
                        "KEY path_index(path(50)))");
//
//                connection.createStatement().execute("CREATE TABLE field(" +
//                        "id INT NOT NULL AUTO_INCREMENT, " +
//                        "name VARCHAR(255) NOT NULL, " +
//                        "selector VARCHAR(255) NOT NULL, " +
//                        "weight FLOAT NOT NULL, " +
//                        "PRIMARY KEY(id))");
                connection.createStatement().execute("CREATE TABLE lemma(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "lemma VARCHAR(255) NOT NULL , " +
                        "frequency INT NOT NULL, " +
                        "site_id INT NOT NULL," +
                        "PRIMARY KEY(id)," +
                        "UNIQUE KEY(lemma(225)));");
                connection.createStatement().execute("CREATE TABLE `index`(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "page_id INT NOT NULL, " +
                        "lemma_id INT NOT NULL, " +
                        "`rank` INT NOT NULL, " +
                        "PRIMARY KEY(id)," +
                        "KEY page_index (page_id)," +
                        "KEY lemma_index (lemma_id)," +
                        "KEY rank_index (`rank`));");



                connection.createStatement().execute("INSERT INTO field(name, selector, weight)" +
                        " VALUES ('title','title',1.0),('body','body',0.8);");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

//
//    public static void executeLemmaInsert() throws SQLException, InterruptedException {
//        String sqlLemma = " INSERT INTO lemma(site_id,lemma, frequency ) " +
//                " VALUES " + insertLemma.toString() +
//                " ON DUPLICATE KEY UPDATE" +
//                " frequency = frequency + 1;";
//
//        DBConnection.getConnection().createStatement().execute(sqlLemma);
//
//        insertLemma = new StringBuffer();
//
//    }
//
//    public static void executeFieldInsert() throws SQLException, InterruptedException {
//        String sqlField = " INSERT INTO field(name, selector, weight)" +
//                " VALUES " + insertField.toString() + ";";
//
//
//        DBConnection.getConnection().createStatement().execute(sqlField);
//        insertField = new StringBuffer();
//    }
//
//    public static void executeSiteInsert() throws SQLException, InterruptedException {
//        String sqlSite = " INSERT INTO site(status , status_time, last_error, url, name)" +
//                " VALUES " + insertSite.toString() + ";";
//
//
//        DBConnection.getConnection().createStatement().execute(sqlSite);
//        insertSite = new StringBuffer();
//    }
//
//    public static void executeIndexInsert() throws SQLException, InterruptedException {
//        String sqlIndex = " INSERT INTO `index`(page_id, lemma_id, `rank`) " +
//                " VALUES " + insertIndex.toString() + ";";
//        DBConnection.getConnection().createStatement().execute(sqlIndex);
//        insertIndex = new StringBuffer();
//
//
//    }
//
//    public static void executePageInsert() throws SQLException, InterruptedException {
//
//        String sqlPage = " INSERT INTO page(site_id ,path, code, content) " +
//                " VALUES " + insertPage.toString() + ";";
//        String sqlErrPage1 = "SET autocommit=0;";
//        String sqlErrPage2 = " INSERT INTO page(site_id ,path, code, content)  VALUES "
//                + insertErrPage.toString() + ";";
//        String sqlErrPage3 = "COMMIT;";
//
//
//        try {
//            DBConnection.getConnection().createStatement().execute(sqlPage);
//
//        } catch (SQLSyntaxErrorException ex) {
//            System.out.println(sqlPage);
//        }
//
//        if (!insertErrPage.toString().isEmpty()) {
//            DBConnection.getConnection().createStatement().execute(sqlErrPage1);
//            DBConnection.getConnection().createStatement().execute(sqlErrPage2);
//            DBConnection.getConnection().createStatement().execute(sqlErrPage3);
//            DBConnection.getConnection().createStatement().execute(sqlErrPage1.replace("0", "1"));
//        }
//        insertPage = new StringBuffer();
//        insertErrPage = new StringBuffer();
//    }
//
//    public static synchronized void addErrToPage(String path, int code, String content, int site_index, int size)
//            throws SQLException, InterruptedException {
//
//
//        if (isIndexPage.isIndexPage()) {
//            for (PageEntity p : pageService.getAllPages()) {
//
//                if (p.getPath().equals(path)) {
//
//                    pageService.updatePageEntityById(p.getId(), path, code, content);
//                } else {
//
//                    SiteEntity site = new SiteEntity();
//
//                    PageEntity page = new PageEntity();
//                    page.setPath(path);
//                    page.setCode(code);
//                    //                    page.setSite_id(site_index);
//
//                    page.setContent(content);
//                    pageService.createPageEntity(page);
//                }
//                break;
//
//            }
//
//        } else {
//
//            insertErrPage.append(insertErrPage.length() == 0 ? "" : ", ").append("(").append(site_index).append(", '")
//                    .append(path).append("', ")
//                    .append(code).append(", '").append(content.replace("'", "@@@")).append("')");
//
//            executePageInsert();
//        }
//
//
//    }
//
//    public static synchronized void addToPage(String path, int code, String content, int site_index, int size)
//            throws SQLException, InterruptedException {
//
//
//        if (isIndexPage.isIndexPage()) {
//
//            for (PageEntity p : pageService.getAllPages()) {
//
//                if (p.getPath().equals(path)) {
//
//                    pageService.updatePageEntityById(p.getId(), path, code, content);
//                } else {
//
//                    PageEntity page = new PageEntity();
//                    page.setPath(path);
//                    page.setCode(code);
////                    page.setSite_id(site_index);
//                    page.setContent(content);
//                    pageService.createPageEntity(page);
//                }
//                break;
//            }
//        } else {
//
//
//            insertPage.append(insertPage.length() == 0 ? "" : ", ").append("(").append(site_index).append(", '")
//                    .append(path).append("', ")
//                    .append(code).append(", '").append(content.replace("'", "@@@")).append("')");
//
//            if (pageCount.incrementAndGet() >= size) {
//
//                executePageInsert();
//            }
//
//        }
//
//
//    }

//    public static synchronized void addToField(String name, String selector, float weight, int size)
//            throws SQLException, InterruptedException {
//        if (isIndexPage.isIndexPage()) {
//
//            for (FieldEntity f : fieldService.getAllFields()) {
//
//                if (!f.getName().equals(name) && f.getWeight() != weight) {
//
//                    FieldEntity field = new FieldEntity();
//                    field.setName(name);
//                    field.setSelector(selector);
//                    field.setWeight(weight);
//                    fieldService.createFieldEntity(field);
//                }
//                break;
//            }
//        } else {
//
//            insertField.append(insertField.length() == 0 ? "" : ", ").append("('").append(name).append("', '")
//                    .append(selector).append("',  ").append(weight).append(")");
//
//            if (pageCount.get() >= size) {
//
//                executeFieldInsert();
//            }
//        }
//    }
//
//    public static synchronized void addToLemma(String lemma, int site_index) throws SQLException,
//            InterruptedException {
//
//        if (isIndexPage.isIndexPage()) {
//
//            for (LemmaEntity l : lemmaService.getAllLemmas()) {
//
//                if (l.getLemma().equals(lemma)) {
//
//                    lemmaService.updateLemmaEntityById(l.getId(), l.getFrequency() + 1);
//                } else {
//
//                    LemmaEntity lemmaEntity = new LemmaEntity();
//                    lemmaEntity.setLemma(lemma);
////                    lemmaEntity.setSite_id(site_index);
//                    lemmaEntity.setFrequency(1);
//                    lemmaService.createlemmaEntity(lemmaEntity);
//                }
//
//                break;
//            }
//        } else {
//
//            insertLemma.append(insertLemma.length() == 0 ? "" : ", ").append("(").append(site_index).append(", '")
//                    .append(lemma).append("', ").append(1).append(")");
//
//            executeLemmaInsert();
//
//        }
//    }
//
//    public static synchronized void addToIndex(int page_id, int lemma_id, float rank) throws SQLException,
//            InterruptedException {
//
//
//        if (isIndexPage.isIndexPage()) {
//
//            for (IndexEntity m : indexService.getAllIndexes()) {
//
//                if (m.getPage_id() != page_id && m.getLemma_id() != lemma_id) {
//
//                    IndexEntity mark = new IndexEntity();
//                    mark.setPage_id(page_id);
//                    mark.setLemma_id(lemma_id);
//                    mark.setRank(rank);
//                    indexService.createIndexEntity(mark);
//                }
//                break;
//            }
//
//        } else {
//
//            insertIndex.append(insertIndex.length() == 0 ? "" : ", ").append("(").append(page_id).append(", ")
//                    .append(lemma_id).append(", ").append(rank).append(")");
//
//            executeIndexInsert();
//
//        }
//    }
//
//    public static synchronized void addToSite(TypesOfIndexes status, String status_time, String last_error,
//                                              String url, String name) throws SQLException, InterruptedException {
//
//        insertSite.append(insertSite.length() == 0 ? "" : ", ").append("('").append(status).append("', '")
//                .append(status_time).append("', '").append(last_error).append("', '").append(url).append("', '")
//                .append(name).append("')");
//
//        executeSiteInsert();
//    }
//
//    public synchronized void pageHib(){
//
//        //             System.out.println(filteredLinks);
//
//
//        PageEntity pageEntity = new PageEntity();
//        String name = way.replace(extension, "");
//        pageEntity.setPath(name.isEmpty() ? "/" : name);
//        pageEntity.setCode(response.statusCode());
//
//        for (SiteEntity siteEntity : siteService.getAllSites()) {
//            if (siteEntity.getId() == site.getOrder()) {
//                pageEntity.setSite_id(siteEntity);
//            }
//        }
//
//        pageEntity.setContent(document.html());
//        pageService.createPageEntity(pageEntity);
//
//
//    }
//    public synchronized void lemmHib(){
//
//        Lemma lemFull = new Lemma(document.text());
//        for (String s : lemFull.collectLemmas()) {
//            LemmaEntity lemma = new LemmaEntity();
//            lemma.setLemma(s);
//            for (SiteEntity siteEntity : siteService.getAllSites()) {
//                if (siteEntity.getId() == site.getOrder()) {
//                    lemma.setSite_id(siteEntity);
//
//                }
//            }
//            lemma.setFrequency(1);
//
//            lemmaService.createlemmaEntity(lemma);
//
//        }
//    }
//
//    public synchronized void siteHib(){
//             SiteEntity siteEntity = new SiteEntity();
//            siteEntity.setStatus(TypesOfIndexes.INDEXING);
//            siteEntity.setName(site.getName());
//            siteEntity.setUrl(site.getUrl());
//            siteEntity.setLast_error("");
//            siteEntity.setStatus_time(LocalDateTime.now());
//            siteService.createSiteEntity(siteEntity);
//     }
}
