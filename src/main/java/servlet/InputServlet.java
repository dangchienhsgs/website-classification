package servlet;

import classficier.DomainClassifier;
import common.Config;
import edu.stanford.nlp.io.EncodingPrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Encoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;



@WebServlet(name = "InputServlet")
public class InputServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //READ DATA FROM HEADER
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null){
            builder.append(line);
        }

        String listDomain[] = builder.toString().split(",");

        PrintWriter writer = response.getWriter();

        try{
            Config config = new Config("configuration.xml");
            DomainClassifier domainClassifier=new DomainClassifier(Arrays.asList(listDomain), config, writer);

            String result = domainClassifier.classify();
            writer.write(result+"\n");
        } catch (Exception e){
            writer.write(e.toString());
            writer.write(e.getMessage());
            writer.write("configuration.xml is not found");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out=response.getWriter();
        out.println ("Hello World");
        out.println(System.getProperty("user.dir"));
    }

}
