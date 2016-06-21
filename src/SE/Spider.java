package SE;

import org.htmlparser.util.ParserException;

import java.io.IOException;
import jdbm.*;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by opw on 27/3/2016.
 */
public class Spider {
    private static final String DB_PATH = "data/database";
    private static Vector<String> DoneList = new Vector<String>();
    private static Queue<String> task = new LinkedList();
    private static int MAXPAGE = 300;

    public static void main(String[] args) {

        System.out.println("SPIDER START...");
        final long startTime = System.currentTimeMillis();

        try {
            fetch("http://www.cse.ust.hk/~ericzhao/COMP4321/TestPages/testpage.htm");
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("PROGRAM RUN FOR %s s\n", (System.currentTimeMillis() - startTime) / 1000d);
        System.out.println("SPIDER END");
    }

    public static void fetch(String url) throws ParserException, IOException {
        if (DoneList.size() < MAXPAGE) {
            if (task.peek() != null)
            {
                task.remove();
            }

            System.out.println("fetching: " + url);
            Indexer indexer = new Indexer(DB_PATH, url);
            Crawler crawler = new Crawler(url);

            //get lastupdate
            Date lastUpdate = crawler.lastUpdate();
            System.out.println("last update:" + lastUpdate);
            //check lastupdate
            if (indexer.pageLastModDateIsUpdated(lastUpdate)) {

                //crawlwer----------------------------------------------------
                Vector<String> links = crawler.extractLinks();
                for (int i = 0; i < links.size(); i++) {
//                    if(!DoneList.contains(links.elementAt(i)))
//                    {
//                        task.add(links.elementAt(i));
//                    }
                    task.add(links.elementAt(i));
                }
                System.out.println("links: "+links);
                int pageSize = crawler.getPageSize();
                System.out.println("pagesize :"+pageSize);
                Vector<String> title = crawler.extractTitle();
                System.out.println("title: " + title);
                // convert title vector to String
                StringBuilder builder = new StringBuilder();

                String prefix = "";
                for(String s : title) {
                    builder.append(prefix);
                    prefix = " ";    // add a spacebar after each word
                    builder.append(s);
                }
                String titleStr = builder.toString();

                Vector<String> word = crawler.extractWords();
                System.out.println("word: " + word);

                //indexer--------------------------------------------------------------
                indexer.insertWords(word);
                indexer.insertTitle(title);
                indexer.insertPageProperty(titleStr, url, lastUpdate, pageSize);
                for (String childUrl : links) {
                    indexer.insertChildPage(childUrl);
                }

                if(!DoneList.contains(url))
                {
                    DoneList.add(url);
                    System.out.print(DoneList.size()+"DDDDDDDDDDDDDone"+url);
                }
            }


            indexer.finalize();

            if (task.peek() != null) {
                try {
                    fetch(task.peek());
                } catch (Exception e) {
                    task.remove();
                    fetch(task.peek());
                }
            }
        }
    }

}
