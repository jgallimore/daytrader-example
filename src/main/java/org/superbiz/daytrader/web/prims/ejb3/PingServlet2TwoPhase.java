/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.superbiz.daytrader.web.prims.ejb3;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.ejb.EJB;

import org.superbiz.daytrader.web.ejb3.TradeSLSBRemote;

import org.superbiz.daytrader.entities.QuoteDataBean;
import org.superbiz.daytrader.utils.Log;
import org.superbiz.daytrader.utils.TradeConfig;

/**
 * 
 * PingServlet2TwoPhase tests key functionality of a TwoPhase commit In this
 * primitive a servlet calls a Session EJB which begins a global txn The Session
 * EJB then reads a DB row and sends a message to JMS Queue The txn is closed w/
 * a 2-phase commit
 * 
 */
@WebServlet(urlPatterns = "/ejb3/PingServlet2TwoPhase")
public class PingServlet2TwoPhase extends HttpServlet {

    private static String initTime;

    private static int hitCount;

    @EJB
    private TradeSLSBRemote tradeSLSBRemote;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        res.setContentType("text/html");
        java.io.PrintWriter out = res.getWriter();
        String symbol = null;
        QuoteDataBean quoteData = null;
        StringBuffer output = new StringBuffer(100);

        output.append("<html><head><title>PingServlet2TwoPhase</title></head>" + "<body><HR><FONT size=\"+2\" color=\"#000066\">PingServlet2TwoPhase<BR></FONT>" + "<FONT size=\"-1\" color=\"#000066\">" + "PingServlet2TwoPhase tests the path of a Servlet calling a Session EJB "
                + "which in turn calls an Entity EJB to read a DB row (quote). The Session EJB " + "then posts a message to a JMS Queue. " + "<BR> These operations are wrapped in a 2-phase commit<BR>");

        try {

            try {
                int iter = TradeConfig.getPrimIterations();
                for (int ii = 0; ii < iter; ii++) {
                    symbol = TradeConfig.rndSymbol();
                    // getQuote will call findQuote which will instaniate the
                    // Quote Entity Bean
                    // and then will return a QuoteObject
                    quoteData = tradeSLSBRemote.pingTwoPhase(symbol);

                }
            } catch (Exception ne) {
                Log.error(ne, "PingServlet2TwoPhase.goGet(...): exception getting QuoteData through Trade");
                throw ne;
            }

            output.append("<HR>initTime: " + initTime).append("<BR>Hit Count: " + hitCount++);
            output.append("<HR>Two phase ping selected a quote and sent a message to TradeBrokerQueue JMS queue<BR>Quote Information<BR><BR>" + quoteData.toHTML());
            out.println(output.toString());

        } catch (Exception e) {
            Log.error(e, "PingServlet2TwoPhase.doGet(...): General Exception caught");
            res.sendError(500, "General Exception caught, " + e.toString());
        }
    }

    public String getServletInfo() {
        return "web primitive, tests Servlet to Session to Entity EJB and JMS -- 2-phase commit path";

    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        hitCount = 0;
        initTime = new java.util.Date().toString();
    }
}