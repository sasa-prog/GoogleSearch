package io.github.sasaprog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        boolean isExit = false;
        List<String> list = null;
        while (!isExit) {
            System.out.print("検索データの取得:1 全ての検索結果をブラウザで表示:2 検索結果を番号で表示:3 検索結果一覧:4 終了:5 >>>");
            String command = sc.nextLine();
            int num = 0;
            try {
                num = Integer.parseInt(command);
            } catch (NumberFormatException e) {
                System.err.println("1 ～ 5の整数を入力してください。");
                continue;
            }
            
            switch (num) {
                case 1:
                    System.out.print("検索キーワード >>>");
                    String keyword = sc.nextLine();
                    list = googleSearch(keyword); 
                    System.out.println(list.size() + "件のデータを取得完了");
                    break;
                case 2:
                    if (list ==null) {
			//TODO: 「先に検索データを取得してください」に変更
                        System.out.println("先に検索をしてください。");
                        break;
                    }
                    try {
                        for (String s:list){
                            ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",s);
                            pb.start();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    if (list ==null) {
                        System.out.println("先に検索をしてください。");
                        break;
                    }
                    System.out.print("何番目の結果？>>>");
                    int index = Integer.parseInt(sc.nextLine());
                    try {
                        ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", list.get(index - 1));
                        pb.start();
                    }catch (IOException e) {
                        System.err.println("エラー:" + e.getMessage());
                    }
                    break;
	case 4:
	    list.parallelStream().forEach((result)->{
	    	String str = getTitle(result);
	    	if (str != null) System.out.println(str);
	    });
	     break;  
    case 5:
        isExit = true;
	     break;
	 default:
	    System.out.println("1～5の整数を指定してください");
            }
        }
        sc.close();
        System.out.println("終了します。");
        
    }
    //Googleでキーワードを検索するメソッド
    /**
     * Googleでキーワードを検索するメソッド
     * @param keyword 検索キーワード
     * @return 検索結果のリンクのリスト
     */
    static List<String> googleSearch(String keyword) {
        List<String> list = new ArrayList<>();
        URL u = null;
        BufferedReader br = null;
        try {
            u = new URL(String.format("https://google.com/search?q=%s", keyword));
            URLConnection conn = u.openConnection();
            conn.setRequestProperty("User-agent","Mozilla/5.0"); 
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String s = null ;
            while ((s = br.readLine()) != null) {
                list.add(s);
            } 
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        List<String> results = analyzeResults(list);
        return results;
    }
    /**
     * HTMLを分析してリンク一覧を取得するメソッド
     * @param list 検索結果のHTMLの各行
     * @return　リンク一覧
     */
    static List<String> analyzeResults(List<String> list) {
        StringTokenizer st =null;
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i< list.size();i++){
            st = new StringTokenizer(list.get(i), "<,>");
            while (st.hasMoreTokens()) {
                String ss = st.nextToken();
                if (ss.indexOf("a") == 0) {
                    String aa =ss.substring(ss.indexOf("herf") != -1?ss.indexOf("href"):0);
                    String aaa = aa.substring(aa.indexOf("\"") + 1 !=0?ss.indexOf("\""):0);
                    String aaaa = aaa.substring(0, aaa.lastIndexOf("\"") != -1?aaa.lastIndexOf("\""):aaa.length());
                    if (aaaa.length() != 0) {
                        String result = aaaa.substring(1);
                        if (result.indexOf("/url") == 0) {
                            results.add(result);
                        }
                    }   
                    
                }
            }
        }
        for (int i = 0; i< results.size();i++) {
            results.set(i, results.get(i).substring(results.get(i).indexOf("h"), results.get(i).indexOf("&")));
        }
        return results;
    }
    static List<String> search(String keyword) {
        return googleSearch(keyword);
    }
    static String getTitle(String url) {
        URL u = null;
        BufferedReader br = null;
        String str = null;
        try {
        	u = new URL(url);
        	URLConnection conn = u.openConnection();
            conn.setRequestProperty("User-agent","Mozilla/5.0"); 
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String s = null ;
            
            while ((s = br.readLine())!=null) {
                if (s.contains("<title>")) {
                	str = s.replace("<title>"," ");
                	str = str.replace("</title>", " ");
                	str = str.trim();
                	break;
                }
            } 
        } catch (IOException e) {
        	System.err.println("エラー:"+e.getMessage());
        }
        return str;
    }
}
