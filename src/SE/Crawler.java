package SE;/* --
COMP4321 Lab2 Exercise
Student Name:
Student ID:
Section:
Email:
*/

import java.io.IOException;
import java.util.Vector;
import org.htmlparser.beans.StringBean;
import java.util.*;
import java.text.*;


//import org.htmlparser.filters.*;
import org.htmlparser.Parser;
import org.htmlparser.filters.RegexFilter;
//import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
//import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;


//import org.w3c.dom.NodeList;
//import org.w3c.dom.Node;
import java.net.HttpURLConnection;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.*;


import org.htmlparser.util.ParserException;
import java.util.StringTokenizer;
import org.htmlparser.beans.LinkBean;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;

public class Crawler {
    private String url;
    Crawler(String _url)
    {
        url = _url;
    }

    Crawler(){
        url = null;
    }


    public String getUrl() {
        return url;
    }

    public Parser getParser() throws ParserException{
        try{
        return (new Parser(url));
        }
        catch(ParserException e){
            e.printStackTrace();
        }
        return null;
    }

    //extract title, url]
    //parents and child
    //date, size

    public int getPageSize() throws IOException
    {
        URL inputLink = new URL(url);
        URLConnection linkConnect = inputLink.openConnection();
        BufferedReader newIn = new BufferedReader(new InputStreamReader(linkConnect.getInputStream()));
        String inputln, temp = "";

        while ((inputln = newIn.readLine()) != null){
            temp += inputln;
        }

        newIn.close();
        return temp.length();
    }

    public Date lastUpdate() throws IOException{
       // String[] urlstr = url.split("://");
        //URL inputLink = new URL("http", urlstr[1], 80, "/");

        URL u = new URL(getUrl());
        URLConnection linkConnect = u.openConnection();

        Date date = new Date(linkConnect.getLastModified());
        //SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
        if(date == null)
            date.setTime(linkConnect.getDate());
        else if(date.toString().equals("1970-01-01"))
            date.setTime(linkConnect.getDate());

           return date;


    }

    public Vector<String> extractTitle() throws ParserException{
        Parser parser = getParser();

        NodeFilter filter = new NodeClassFilter(TitleTag.class);
        NodeList nodelist =  parser.parse(filter);
        String str ="";

        for(int i = 0; i < nodelist.size(); i++){
            Node node = nodelist.elementAt(i);
            if(node instanceof TitleTag) {
                TitleTag titletag = (TitleTag) node;
                str = titletag.getTitle();
            }
        }
        String[] strsplit = str.split(" ");

        Vector<String> title = new Vector<>();
        for(int i = 0; i < strsplit.length;i++) title.add(strsplit[i]);

        return title;
    }

    public Vector<String> extractWords() throws ParserException
    {
        // extract words in url and return them
        // use StringTokenizer to tokenize the result from StringBean
        // ADD YOUR CODES HERE
        Vector<String> v_word = new Vector<String>();
        StringBean sb = new StringBean();

        // StringBean settings
        sb.setLinks (false);
        sb.setReplaceNonBreakingSpaces (true);
        sb.setCollapse (true);
        sb.setURL (url);

        String s = sb.getStrings();
        s = s.replaceAll("[,:!/.%|()-+&^#@*']", "");
//        System.out.print(s);
//        System.out.print(s);
        StringTokenizer st = new StringTokenizer(s);


        while (st.hasMoreTokens())
            v_word.add(st.nextToken());

        return v_word;
    }

    public Vector<String> extractLinks() throws ParserException
    {
        // extract links in url and return them
        // ADD YOUR CODES HERE
        LinkBean lb = new LinkBean();
        lb.setURL(this.url);
        URL[] URL_array = lb.getLinks();

        Vector<String> v_link = new Vector<String>();
        for(int i = 0; i < URL_array.length; i++){
            v_link.add(URL_array[i].toString());
        }
        return v_link;
    }

   public static void main(String[] args)
    {
        try
        {
            Crawler crawler = new Crawler("http://course.cse.ust.hk/comp3021");

            int  NumPages = crawler.getPageSize();
            System.out.println("It has number of pages: "+ NumPages);

            Date lastDate = crawler.lastUpdate();
            System.out.println("Lat update on: "+ lastDate);

            Vector<String> words = crawler.extractWords();
            System.out.println("Words in "+crawler.getUrl()+":");
            for(int i = 0; i < words.size(); i++)
                System.out.print(words.get(i)+" ");
            System.out.println("\n\n");

            Vector<String> links = crawler.extractLinks();
            System.out.println("Links in "+crawler.getUrl()+":");
            for(int i = 0; i < links.size(); i++)
                System.out.println(links.get(i));
            System.out.println("");

            Vector<String> title = crawler.extractTitle();
            System.out.println("Words in "+crawler.getUrl()+":");
            System.out.print("title: ");
            for(int i = 0; i < title.size(); i++)
                System.out.print(title.get(i)+" ");


        }
        catch (ParserException e)
        {
            e.printStackTrace ();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }



}

