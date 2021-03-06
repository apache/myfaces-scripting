<?xml version="1.0" ?>
<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="section">#<xsl:attribute name="name"></xsl:attribute><xsl:value-of select="@name"/>
        <xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subsection">## <xsl:attribute name="name"/><xsl:value-of select="@name"/>
        <xsl:apply-templates/>
	</xsl:template>

    <xsl:template match="subsubsection">## <xsl:attribute name="name"/><xsl:value-of select="@name"/>
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="subsubsubsection">### <xsl:attribute name="name"/><xsl:value-of select="@name"/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="ul">
        <xsl:copy-of select="." />
    </xsl:template>

    <!-- enable if you want the template list handling nested lists do not work
    due to the code formatter we have to run afterwards hence this is disabled per
    default, if you do not have nested lists feel free to run it
    <xsl:template match="ul/li">* <xsl:value-of select="." /></xsl:template>
    <xsl:template match="ul/ul/li"> * <xsl:value-of select="." /></xsl:template>
    <xsl:template match="ul/ol/li"> - <xsl:value-of select="." /></xsl:template>


    <xsl:template match="ol/li">- <xsl:value-of select="." /></xsl:template>
    <xsl:template match="ol/ol/li"> - <xsl:value-of select="." /></xsl:template>
    <xsl:template match="ol/ul/li"> * <xsl:value-of select="." /></xsl:template>
    -->

    <xsl:template match="source">
       <code>
           <!-- hidden line break so that code definitely is on its own line -->
           <xsl:text>&#xA;</xsl:text>
           <xsl:value-of disable-output-escaping="yes" select="."/>
           <xsl:text>&#xA;</xsl:text>
       </code>
    </xsl:template>

    <xsl:template match="a"><xsl:attribute name="href"/>[<xsl:value-of disable-output-escaping="no" select="." />](<xsl:value-of select="@href"/>)</xsl:template>
    <xsl:template match="table">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="iframe">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="img"><xsl:attribute name="alt" /><xsl:attribute name="src" />![<xsl:value-of select="@alt"/>](<xsl:value-of select="@src"/>)</xsl:template>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
    <xsl:template match="properties"/>
    <xsl:output encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
</xsl:stylesheet>
