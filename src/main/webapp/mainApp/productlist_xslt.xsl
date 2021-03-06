<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:template match="productlist">
        <table border="0" width="840">
            <tr bgcolor="orange" cellspacing="0">
    
                <td>
                    <strong>Brand</strong>
                </td>
 
                <td>
                    <strong>Description</strong>
                </td>
                <td>
                    <strong>Price</strong>
                </td>
                
                <td>
                    <strong>Amount</strong>
                </td>
                
            </tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
  
    <xsl:template match="product">
        <form action="shop">
            <tr bgcolor="#E8E8E8" >
                <td>
    <!--The variable header is used to save the selected product name -->
                    <xsl:variable name="header"> 
                        <xsl:value-of select="brand"/>
                    </xsl:variable>
             
                    <xsl:value-of select="brand"/>
                    <input name="selected" type="hidden"  value="{$header}"/>
            
                </td>
                <td>
                    <xsl:value-of select="description"/>
            
                </td>
                    
                <td>
                    <xsl:value-of select="price"/>
            
                </td>
                
                <td>
                    <xsl:value-of select="available"/>
            
                </td>
                
        
                <td>
                    <xsl:element name="input"> 
                        <xsl:attribute name="size">1</xsl:attribute>
                        <xsl:attribute name="type">text</xsl:attribute>
                        <xsl:attribute name="value">1</xsl:attribute>
                        <xsl:attribute name="name">quantity</xsl:attribute>
                    </xsl:element>        
                </td>
                <td>
                    <xsl:variable name= "selectedId">
                        <xsl:value-of select="id"/>
                    </xsl:variable>
                    <input type="submit" value="Select"/>
                   
                    <input name="selectedProduct" type="hidden"  value="{$selectedId}"/>
                </td>
                <td>
                    
                    
                        <xsl:element name="a"> <!-- A link in XSLT -->
                            <xsl:attribute name="href">
                                <xsl:text disable-output-escaping="yes"><![CDATA[shop?action=detail&productid=]]></xsl:text><xsl:value-of select="id"/></xsl:attribute>
                            <xsl:text>Detail</xsl:text>
                        </xsl:element>
                    
                  
                    
                    
                </td>
                
                
            </tr>
    
    
            <xsl:element name="input"> <!--A ordinary input in XSLT-->
                <xsl:attribute name="type">hidden</xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="id"/>
                </xsl:attribute>
                <xsl:attribute name="name">productid</xsl:attribute>
            </xsl:element>
    
    
            
            <input name="action" type="hidden"  value="productShow"/>
            <input name="actionComp" type="hidden"  value="productCompShow"/>
    
    
    
        </form>
    </xsl:template>
  
</xsl:stylesheet>
