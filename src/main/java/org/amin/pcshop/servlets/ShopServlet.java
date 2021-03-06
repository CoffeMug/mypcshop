package org.amin.pcshop.servlets;
/*
 * Shop.java
 *
 */
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.amin.pcshop.domain.*;

/**
 * This is the main servlet of the application which controls almost 
 * all of the functionality of the project, especially the shopping part is under
 * the control of this servlet.
 *
 * @author  Amin & Soode
 *
 *
 */
public class ShopServlet extends HttpServlet {


    // List of initialization variables
    private static String thankyouPage = null;
    private static String profileChange = null;
    private static String profilePage = null;
    private static String userPage = null;
    private static String jdbcURL = null;

    private static String redirectPage = null;
    private static String productPage = null;
    private static String productCompPage = null;
    private static String homePage = null;


    private ComponentList compList = null;
    private ProductList productList = null;

    /////////////////////////////////////



    /**
     * Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // look up our aliases for all pages, these are mapped
        // in web.xml


        productPage = config.getInitParameter("PRODUCT_PAGE");
        productCompPage = config.getInitParameter("PRODUCT_COMPONENT_PAGE");
        thankyouPage = config.getInitParameter("THANKYOU_PAGE");
        profilePage = config.getInitParameter("PROFILE_PAGE");
        userPage = config.getInitParameter("USER_PAGE");
        jdbcURL = config.getInitParameter("JDBC_URL");
        redirectPage = config.getInitParameter("CHECKOUT_REDIRECT_PAGE");
        homePage = config.getInitParameter("HOME_PAGE");
        profileChange = config.getInitParameter("PROFILE_CHANGE");

        // get the component list from the database using a bean
        try{
            compList = new ComponentList(jdbcURL);
        }
        catch(Exception e){
            throw new ServletException(e);
        }


        // get the product list from the database using a bean

        try{
            productList = new ProductList(jdbcURL);
        }
        catch(Exception e){
            throw new ServletException(e);
        }




        // servletContext is the same as scope Application
        // store the complist in application scope

        ServletContext sc = getServletContext();
        sc.setAttribute("compList", compList);
        sc.setAttribute("productList", productList);

    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {

    }

    /** Processes requests for both HTTP <code>GET</code>
     and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, java.io.IOException {

        // This is the dispatcher in our application
        // All requests will go through it.

        // Get access to the session and to the shoppingcart
        // Store the logged in username in the session
        // And get jdbc-URL provided in web.xml as init-parameter

        HttpSession sess = request.getSession();
        RequestDispatcher rd = null;
        ShoppingCart shoppingCart = getCart(request);
        sess.setAttribute("currentUser", request.getRemoteUser());
        sess.setAttribute("jdbcURL",jdbcURL);

        String errorMessage = " ";
        String url= " ";

        // find out what to do based on the attribute "action"
        // no action or show

        if(request.getParameter("action") == null){
            rd = request.getRequestDispatcher(productPage);
            rd.forward(request,response);

        }


        else if (request.getParameter("action").equals("show")){

            // A request dispatcher that's connected to the page.

            try {
                productList = new ProductList(jdbcURL);
            } catch (Exception e) {
                throw new ServletException(e);
            }

            ServletContext sc = getServletContext();
            sc.setAttribute("productList", productList);

            rd = request.getRequestDispatcher(productPage);
            rd.forward(request,response);
        }



        else if(request.getParameter("action").equals("productShow")){

            // verify productid and quantity

            if (request.getParameter("productid") != null &&
                    request.getParameter("quantity")!=null ){

                Product pb = null;

                // search the component in our shop

                pb = productList.getById(Integer.parseInt(
                        request.getParameter("productid")));


                // Here we check some criteria for the quantity of the product slected
                // By the user.
                // We use servelet for data validation on the server side!

                if(Integer.parseInt(request.getParameter("quantity"))> pb.getAvailabe()||
                        Integer.parseInt(request.getParameter("quantity"))<= 0){

                    errorMessage = "Please Select a correct amount of products";
                    url = "/mainApp/productDetail.jsp";


                    request.setAttribute("message", errorMessage);

                    RequestDispatcher dispatch =
                            getServletContext().getRequestDispatcher(url);
                    dispatch.forward(request, response);

                }else{

                    if(pb == null){
                        throw new ServletException("The component is not in stock.");

                    }
                    else {

                        // Here we remove the selected quantity of the product temporarily
                        // And if the purchase is done then this will also remove
                        // from database permanently


                        Collection tmpProductList = (ArrayList)productList.getProductList();
                        tmpProductList.remove(pb);

                        pb.setAvailable(pb.getAvailabe()-Integer
                                .parseInt(request.getParameter("quantity")));

                        tmpProductList.add(pb);

                        productList.setProductList(tmpProductList);

                        shoppingCart.addProduct(pb,Integer.parseInt(
                                request.getParameter("quantity")));

                        ServletContext sc = getServletContext();
                        sc.setAttribute("productList", productList);


                    }


                    // back to the showpage
                    request.setAttribute("message", " ");
                    rd = request.getRequestDispatcher(productPage);
                    rd.forward(request,response);
                }
            }
        }

        // remove a product from the cart

        else if(request.getParameter("action").equals("remove")){
            if (request.getParameter("productid") != null &&
                    request.getParameter("quantity")!=null ){
                shoppingCart.removeProduct(
                        Integer.parseInt(request.getParameter("productid")),
                        Integer.parseInt(request.getParameter("quantity")));



                // Here we add the quantity of the product which is removed
                // From shopping cart by the user to the amount of the current product
                //
                Product pb = null;

                // search the component in our shop

                pb = productList.getById(Integer.parseInt(
                        request.getParameter("productid")));

                Collection tmpProductList = (ArrayList)productList.getProductList();
                tmpProductList.remove(pb);

                pb.setAvailable(pb.getAvailabe()+ Integer
                        .parseInt(request.getParameter("quantity")));

                tmpProductList.add(pb);

                productList.setProductList(tmpProductList);



                ServletContext sc = getServletContext();
                sc.setAttribute("productList", productList);




            }
            else{
                throw new ServletException(
                        "No productid or quantity when removing component from cart");
            }



            rd = request.getRequestDispatcher(productPage);
            rd.forward(request,response);
        }

        // detailed information about a component

        else if(request.getParameter("action").equals("detail")){
            if (request.getParameter("productid") != null){

                // find the product, store a reference in our request

                Product pb = productList.getById(
                        Integer.parseInt(request.getParameter("productid")));
                request.setAttribute("productid", pb);
            }
            else{
                throw new ServletException("No productid when viewing detail");
            }

            /**
             * We defined this to reload the bean which is used to show the
             * list of components of the product
             */
            try {
                compList = new ComponentList(jdbcURL);
            } catch (Exception e) {
                throw new ServletException(e);
            }
            ServletContext sc = getServletContext();
            sc.setAttribute("compList",compList);


            rd = request.getRequestDispatcher(productCompPage);
            rd.forward(request,response);
        }

        // make an order from our cart, empty the cart

        else if(request.getParameter("action").equals("save")){

            // if we have a shoppingcart, verify that we have
            // valid userdata, then create an orderbean and
            // save the order in the database

            if (shoppingCart != null &&
                    request.getParameter("shipping_name") != null &&
                    request.getParameter("shipping_city") != null &&
                    request.getParameter("shipping_zipcode") != null &&
                    request.getParameter("shipping_address") != null){
                Order ob = new Order(jdbcURL, shoppingCart,
                        request.getParameter("shipping_name").trim(),
                        request.getParameter("shipping_address").trim(),
                        request.getParameter("shipping_zipcode").trim(),
                        request.getParameter("shipping_city").trim());
                try{
                    ob.saveOrder();
                }
                catch(Exception e){
                    throw new ServletException("Error saving order", e);
                }
            }
            else{
                throw new ServletException(
                        "Not all parameters are present or no " +
                                " shopping cart when saving component");
            }
            rd = request.getRequestDispatcher(thankyouPage);
            rd.forward(request,response);
        }

        // checkout, get user data, we must have a valid user

        else if(request.getParameter("action").equals("checkout")){

            if(shoppingCart.getCart().isEmpty()){

                errorMessage = "Your Shopping Cart Is Empty!";
                url = "/mainApp/productDetail.jsp";


                request.setAttribute("messageEmptyCart", errorMessage);

                RequestDispatcher dispatch =
                        getServletContext().getRequestDispatcher(url);
                dispatch.forward(request, response);

            }
            else{


                if(sess.getAttribute("currentUser") != null) {

                    // create a profile and populate it from the
                    // database

                    Profile p = new Profile(jdbcURL);
                    try {
                        p.populate((String)sess.getAttribute("currentUser"));
                    }
                    catch(Exception e) {
                        throw new ServletException("Error loading profile", e);
                    }
                    sess.setAttribute("profile", p);

                }

                // redirect (not forward)

                response.sendRedirect(redirectPage);
            }
        }

        // logout, just delete the session (where we have the user data)

        else if(request.getParameter("action").equals("logout")) {
            sess.invalidate();
            rd = request.getRequestDispatcher(homePage);
            rd.forward(request,response);
        }

        // get a user profile from the database and store it in
        // our session

        else if(request.getParameter("action").equals("profile")) {
            HashMap<String,Boolean> role = null;

            // create a profile object, fill it in from the database
            // also store all user roles in the map "role"

            Profile p = new Profile(jdbcURL);
            try {
                p.populate((String)sess.getAttribute("currentUser"));
                role = p.getRoles();
            }
            catch(Exception e) {
                throw new ServletException("Error loading profile", e);
            }
            sess.setAttribute("profile", p);

            // flag all roles that the user is associated with

            Set<String> k = role.keySet();
            Iterator<String> i = k.iterator();
            while (i.hasNext()) {
                String st = i.next();
                if(request.isUserInRole(st)) role.put(st,true);
            }
            p.setRole(role);
            sess.setAttribute("roles",role);
            rd = request.getRequestDispatcher(profilePage);
            rd.forward(request, response);
        }


        // update the user profile or create a new profile
        // the profile is already create but may be empty

        else if(request.getParameter("action").equals("profilechange") ||
                request.getParameter("action").equals("usercreate")){
            Profile pb = (Profile)sess.getAttribute("profile");
            String u;
            if (request.getParameter("action").equals("profilechange"))
                //u = (String)sess.getAttribute("currentUser");
                u = request.getParameter("user");
            else
                u = request.getParameter("user");

            // get all data needed

            String p1 = request.getParameter("password");
            String p2 = request.getParameter("password2");
            String name = request.getParameter("name");
            String street = request.getParameter("street");
            String zip = request.getParameter("zip");
            String city = request.getParameter("city");
            String country = request.getParameter("country");

            pb.setUser(u);
            pb.setPassword(p1);
            pb.setName(name);
            pb.setStreet(street);
            pb.setZip(zip);
            pb.setCity(city);
            pb.setCountry(country);
            HashMap<String, Boolean> r =
                    (HashMap<String,Boolean>) sess.getAttribute("roles");
            Set<String> k = r.keySet();
            Iterator<String> i = k.iterator();
            while (i.hasNext()) {
                String st = i.next();
                String res = request.getParameter(st);
                if (res != null) r.put(st, true);
                else r.put(st,false);
            }
            pb.setRole(r);

            // if this is a new user, try to add him to the database

            if (request.getParameter("action").equals("usercreate")) {
                boolean b;

                // make sure the the username is not used already

                try {
                    b = pb.testUser(u);
                }
                catch(Exception e) {
                    throw new ServletException("Error loading user table", e);
                }
                if(b) {
                    sess.setAttribute("passwordInvalid",
                            "User name already in use");
                    rd = request.getRequestDispatcher(userPage);
                    rd.forward(request, response);

                    // note that a return is needed here because forward
                    // will not cause our servlet to stop execution, just
                    // forward the request processing

                    return;
                }
            }

            // now we know that we have a valid user name
            // validate all data,

            boolean b = profileValidate(request,sess);
            if (!b && request.getParameter("action").equals("profilechange")) {
                rd = request.getRequestDispatcher(profilePage);
                rd.forward(request, response);
            }
            else if (!b) {
                rd = request.getRequestDispatcher(userPage);
                rd.forward(request, response);
            }

            // validated OK,  update the database

            else {

                ProfileUpdate pu = new ProfileUpdate(jdbcURL);
                if (request.getParameter("action").equals("profilechange")) {
                    try {
                        pu.setProfile(pb);
                    }
                    catch(Exception e){
                        throw new ServletException("Error saving profile", e);
                    }
                    rd = request.getRequestDispatcher(profileChange);
                    rd.forward(request, response);
                }
                else {
                    try {
                        pu.setUser(pb);
                    }
                    catch(Exception e){
                        throw new ServletException("Error saving profile", e);
                    }
                    response.sendRedirect(redirectPage);
                }
            }
        }

        // create an empty profile, store in the in session
        // with all available roles

        else if(request.getParameter("action").equals("newuser")) {
            Profile p = new Profile(jdbcURL);
            try {
                HashMap<String,Boolean> role = p.getRoles();
                sess.setAttribute("roles",role);
            }
            catch(Exception e) {
                throw new ServletException("Error loading profile", e);
            }

            sess.setAttribute("profile", p);

            rd = request.getRequestDispatcher(userPage);
            rd.forward(request, response);
        }
       
       /*
        else if (request.getParameter("action").equals("deleteCookie")){
       
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                cookie.setMaxAge(0); //delete the cookie
                cookie.setPath("/"); //allow the entire application to access it
                response.addCookie(cookie);
            }

            String urltmp = "/delete_cookies.jsp";
            RequestDispatcher dispatcher =
                    getServletContext().getRequestDispatcher(urltmp);
            dispatcher.forward(request, response);
   
        }
       */

    }


    // validate a profile

    private boolean profileValidate(HttpServletRequest request,
                                    HttpSession sess) {

        // use the attribute "passwordInvalid" as error messages

        sess.setAttribute("passwordInvalid", null);
        String u;

        // get all data

        if (request.getParameter("action").equals("profilechange"))
            //u = (String)sess.getAttribute("currentUser");
            u = request.getParameter("user");
        else
            u = request.getParameter("user");
        String p1 = request.getParameter("password");
        String p2 = request.getParameter("password2");
        String name = request.getParameter("name");
        String street = request.getParameter("street");
        String zip = request.getParameter("zip");
        String city = request.getParameter("city");
        String country = request.getParameter("country");
        HashMap<String,Boolean> r =
                (HashMap<String,Boolean>) sess.getAttribute("roles");
        Set<String> k = r.keySet();
        int count=0;
        Iterator<String> i = k.iterator();
        while (i.hasNext()) {
            String st = request.getParameter(i.next());
            if(st != null) count++;
        }

        // validate

        if(count == 0) {
            sess.setAttribute("passwordInvalid",
                    "You must select at least one role");
            sess.setAttribute("currentUser",u);

            return false;
        }
        else if( u == null || u.length() < 1) {
            sess.setAttribute("passwordInvalid",
                    "User name must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;

        }
        if(!request.isUserInRole("admin") &&
                request.getParameter("admin") != null) {
            sess.setAttribute("passwordInvalid",
                    "You must be in role admin to set role admin");
            sess.setAttribute("currentUser",u);
            return false;
        }
        if(p1 == null || p2 == null || p1.length() < 1) {
            sess.setAttribute("passwordInvalid",
                    "Password must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;

        }
        else if (!(p1.equals(p2))){
            sess.setAttribute("passwordInvalid",
                    "Passwords do not match, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }
        else if (name == null || name.length() < 1){
            sess.setAttribute("passwordInvalid",
                    "Name must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }
        else if (street == null || street.length() < 1){
            sess.setAttribute("passwordInvalid",
                    "Street must no be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }
        else if (zip == null || zip.length() < 1){
            sess.setAttribute("passwordInvalid",
                    "Zip code must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }
        else if (city == null || city.length() < 1){
            sess.setAttribute("passwordInvalid",
                    "City must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }
        else if (country == null || country.length() < 1){
            sess.setAttribute("passwordInvalid",
                    "County must not be empty, retry!");
            sess.setAttribute("currentUser",u);
            return false;
        }

        // validation OK

        return true;
    }

    // get the shoppingcart, create it if needed

    private ShoppingCart getCart(HttpServletRequest request){
        HttpSession se = null;
        se=request.getSession();
        ShoppingCart sb =null;
        sb = (ShoppingCart)se.getAttribute("shoppingCart");
        if(sb==null){
            sb = new ShoppingCart();
            se.setAttribute("shoppingCart",sb);
        }

        return sb;
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "The main PC Shop";
    }
}


