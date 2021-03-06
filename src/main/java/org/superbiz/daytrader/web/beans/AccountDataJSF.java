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
package org.superbiz.daytrader.web.beans;

import java.math.BigDecimal;
//import java.util.Collection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.superbiz.daytrader.core.direct.FinancialUtils;
import org.superbiz.daytrader.entities.AccountDataBean;
import org.superbiz.daytrader.entities.HoldingDataBean;
import org.superbiz.daytrader.entities.OrderDataBean;

import org.superbiz.daytrader.utils.TradeConfig;
import org.superbiz.daytrader.web.TradeAction;

@ManagedBean(name="accountdata")

public class AccountDataJSF {    
    private Date sessionCreationDate;
    private Date currentTime;
    private String profileID;    
    private Integer accountID;
    private Date creationDate;
    private int loginCount;
    private Date lastLogin;
    private int logoutCount;
    private BigDecimal balance;
    private BigDecimal openBalance;    
    private Integer numberHoldings;
    private BigDecimal holdingsTotal;
    private BigDecimal sumOfCashHoldings;
    private BigDecimal gain;
    private BigDecimal gainPercent;    
    
    private OrderData[] closedOrders;
    private OrderData[] allOrders;
    
    public AccountDataJSF(){
        home();
    }
    
    public Date getSessionCreationDate() {
        return sessionCreationDate;
    }
    
    public void setSessionCreationDate(Date sessionCreationDate) {
        this.sessionCreationDate = sessionCreationDate;
    }
    
    public Date getCurrentTime() {
        return currentTime;
    }
    
    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }
    
    public String getProfileID() {
        return profileID;
    }
    
    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }
    

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setOpenBalance(BigDecimal openBalance) {
        this.openBalance = openBalance;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }    

    public void setHoldingsTotal(BigDecimal holdingsTotal) {
        this.holdingsTotal = holdingsTotal;
    }

    public BigDecimal getHoldingsTotal() {
        return holdingsTotal;
    }

    public void setSumOfCashHoldings(BigDecimal sumOfCashHoldings) {
        this.sumOfCashHoldings = sumOfCashHoldings;
    }

    public BigDecimal getSumOfCashHoldings() {
        return sumOfCashHoldings;
    }

    public void setGain(BigDecimal gain) {
        this.gain = gain;
    }

    public BigDecimal getGain() {
        return gain;
    }

    public void setGainPercent(BigDecimal gainPercent) {
        this.gainPercent = gainPercent;
    }

    public BigDecimal getGainPercent() {
        return gainPercent;
    }

    public void setNumberHoldings(Integer numberHoldings) {
        this.numberHoldings = numberHoldings;
    }

    public Integer getNumberHoldings() {
        return numberHoldings;
    }
    
    public void home(){
        TradeAction tAction = new TradeAction();
        try {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);    
        String userID = (String)session.getAttribute("uidBean");        
        AccountDataBean accountData = tAction.getAccountData(userID);
        Collection<HoldingDataBean> holdingDataBeans = tAction.getHoldings(userID);
        java.util.Collection closedOrders = tAction.getClosedOrders(userID);        
        
        if ( (closedOrders!=null) && (closedOrders.size() > 0) ) {
            session.setAttribute("closedOrders", closedOrders);
            OrderData[] orderjsfs = new OrderData[closedOrders.size()];
            Iterator it = closedOrders.iterator();
            int i = 0;
            while (it.hasNext() )
            {
                OrderDataBean order = (OrderDataBean)it.next();
                OrderData r = new OrderData(order.getOrderID(),order.getOrderStatus(), order.getOpenDate(), order.getCompletionDate(), order.getOrderFee(), order.getOrderType(), order.getQuantity(),order.getSymbol());
                orderjsfs[i] = r;
                i++;
            }
            setClosedOrders(orderjsfs);
        }
        
        ArrayList orderDataBeans = (TradeConfig.getLongRun() ? new ArrayList() : (ArrayList) tAction.getOrders(userID));
        if ( (orderDataBeans!=null) && (orderDataBeans.size() > 0) ) {
            session.setAttribute("orderDataBeans", orderDataBeans);
            OrderData[] orderjsfs = new OrderData[orderDataBeans.size()];
            Iterator it = orderDataBeans.iterator();
            int i = 0;
            while (it.hasNext() )
            {
                OrderDataBean order = (OrderDataBean)it.next();
                OrderData r = new OrderData(order.getOrderID(),order.getOrderStatus(), order.getOpenDate(), order.getCompletionDate(), order.getOrderFee(), order.getOrderType(), order.getQuantity(),order.getSymbol());
                orderjsfs[i] = r;
                i++;
            }
            setAllOrders(orderjsfs);
        }
        
        setSessionCreationDate((Date)session.getAttribute("sessionCreationDate"));
        setCurrentTime(new java.util.Date());
        doAccountData(accountData, holdingDataBeans);        
        
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /*public String quotes(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        session.setAttribute("symbols", symbols);        
        return "Quotes";
    }*/
    
    private void doAccountData(AccountDataBean accountData, Collection<HoldingDataBean> holdingDataBeans){
        setProfileID(accountData.getProfileID());
        setAccountID(accountData.getAccountID());
        setCreationDate(accountData.getCreationDate());
        setLoginCount(accountData.getLoginCount());
        setLogoutCount(accountData.getLogoutCount());
        setLastLogin(accountData.getLastLogin());
        setOpenBalance(accountData.getOpenBalance());
        setBalance(accountData.getBalance());        
        setNumberHoldings(holdingDataBeans.size());
        setHoldingsTotal(FinancialUtils.computeHoldingsTotal(holdingDataBeans));
        setSumOfCashHoldings(balance.add(holdingsTotal));
        setGain(FinancialUtils.computeGain(sumOfCashHoldings, openBalance));
        setGainPercent(FinancialUtils.computeGainPercent(sumOfCashHoldings, openBalance));
        
    }
    
    
    public OrderData[] getClosedOrders(){
        return closedOrders;        
    }

    public void setClosedOrders(OrderData[] closedOrders) {
        this.closedOrders = closedOrders;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLogoutCount(int logoutCount) {
        this.logoutCount = logoutCount;
    }

    public int getLogoutCount() {
        return logoutCount;
    }

    public void setAllOrders(OrderData[] allOrders) {
        this.allOrders = allOrders;
    }

    public OrderData[] getAllOrders() {
        return allOrders;
    }
}
