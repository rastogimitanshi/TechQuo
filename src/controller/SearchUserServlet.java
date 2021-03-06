package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import domain.User;
import org.apache.log4j.Logger;
import com.mysql.jdbc.Connection;

import connection.ConnectionManager;

public class SearchUserServlet extends HttpServlet{

	private static final long serialVersionUID =102831973239L;
	final static Logger logger= Logger.getLogger(UpdateProfileServlet.class);
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	logger.info("Inside Search User servlet");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
        	Connection conn=null;
			conn = ConnectionManager.getConnection();
            Integer Uid = Integer.parseInt(request.getParameter("UID"));
            System.out.println(Uid);
            PreparedStatement pstU= (PreparedStatement) conn.prepareStatement("UPDATE `user` SET Profile_views= Profile_views+1 WHERE user_id=?");
            pstU.setInt(1,Uid);
            int i = pstU.executeUpdate();
            if(i>0){
            PreparedStatement pst= (PreparedStatement) conn.prepareStatement("SELECT Fname, Lname, Email, DOB,"
            			+"Country, City, JobPosition, Profile_views, QuestionsPosted, AnswersAnswered FROM"+
            		"`user` WHERE user_id=?");
            pst.setInt(1, Uid);
            ResultSet rs=pst.executeQuery();
            rs.next();
            User user =new User(Uid,rs.getString(1),rs.getString(2),rs.getString(5),rs.getString(3),rs.getString(6),
            		rs.getString(7),rs.getDate(4),"","");
            user.setAnswersAnswered(rs.getInt(10));
            user.setQuestionsPosted(rs.getInt(9));
            Integer pv=rs.getInt(8);
            PreparedStatement pstI= (PreparedStatement) conn.prepareStatement("SELECT Interests FROM `interests_user`"+
            					"WHERE user_id_I=?");
            pstI.setInt(1, Uid);
            String Interests="";
            ResultSet rstI=pstI.executeQuery();
            if(rstI.next())
            {
                Interests= Interests.concat(rstI.getString(1)).concat(",");
            
            while(rstI.next()){
            	Interests= Interests.concat(rstI.getString(1)).concat(",");
            }
            }
            System.out.println(Interests);
            PreparedStatement pstE= (PreparedStatement) conn.prepareStatement("SELECT Education FROM `education_user`"+
					"WHERE user_id_E=?");
            pstE.setInt(1, Uid);
            String Education="";
            ResultSet rstE=pstE.executeQuery();
            if(rstE.next())
            {
                Education= Education.concat(rstE.getString(1)).concat(", ");
            
            while(rstE.next()){
            Education= Education.concat(rstE.getString(1)).concat(",");
            }
            }
            
            System.out.println(Education);
            request.setAttribute("UserDetails", user);
            request.setAttribute("Interests",Interests);
            request.setAttribute("Education",Education);
            request.setAttribute("ProfileViews",pv);
            RequestDispatcher requestDispatcher;			
    		requestDispatcher =request.getRequestDispatcher("UserProfile.jsp"); //change name later
    		requestDispatcher.forward(request, response); 
		
            }
        }
            

        catch(Exception e){
        	e.printStackTrace();
			logger.error(e);
			logger.info("Some Error Occured");
			out.println("<script type=\"text/javascript\">");        // creating alert message using java
			out.println("alert('Some unexpected error occured. Please try again later');");
			out.println("location='Home.jsp';");
			out.println("</script>");
        }
    }
}

