package servlet;

import classficier.DomainClassifier;
import common.Config;
import common.RequestDomain;
import utils.DatabaseChecker;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "RequestDomainInfoServlet")
public class RequestDomainInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // read data from header
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null){
            builder.append(line);
        }

        // split that header into domains
        List<String> listDomain = Arrays.asList(builder.toString().split(","));

        // return domains informations
        PrintWriter writer = response.getWriter();

        try {
            Config config = new Config("configuration.xml");
            RequestDomain requestDomain = new RequestDomain(listDomain, config);

            String result = requestDomain.getDomainInfo();
            writer.write(result);

        } catch (Exception e){
            writer.write(e.toString());
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
