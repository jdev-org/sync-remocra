<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:template match="rapport" >
	<html>
    <head>
		<meta harset="utf-8"/>
		<title></title>
		<style type="text/css">
		
		*{
			font-family: Arial,Helvetica Neue,Helvetica,sans-serif; 
		}
		
		body{
			background-color: none;
		}
		
		table {
			border-collapse: collapse;
			width:100%
		}
		
		th, td{
			font-size: 0.9em;
			border: 1px solid darkgrey;
			padding:5px
		}
		
		th {
			background-color: silver;
		}
		
		td {
			text-align: left;
		}
		
		a {
			display: block;
		}
		
		a, p, li{
			font-size: 0.9em;
			color : black;
		}
		
		a:hover{
			color : red;
		}
		
		h1{
			font-size: 1em;
		}

		</style>
    </head>
	<body>
	<div>
		<h1>Synchronisation de données vers REMOCRA</h1>
		<p>Le système automatisé de transfert de données entre EDP et REMOCRA n'a pas pu réaliser automatiquement les syncronisations de données suivantes. Merci de prendre en charge les actions mentionnées ci-dessous.</p>
		<xsl:apply-templates select="erreurs"/>
	</div>
</body>
</html>
</xsl:template>

<xsl:template match="erreurs" >
	<table>
		<tr>
			<th style="width:10%">Date et heure</th>
			<th style="width:10%">Ref. PEI</th>
			<th style="width:30%">Erreur</th>
			<th style="width:50%">Action proposée</th>
		</tr>
		<xsl:apply-templates select="erreur"/>
	</table>
</xsl:template>

<xsl:template match="erreur" >
	<tr>
		<td style="width:10%">
			<xsl:value-of select="dateHeureSynchronisation"/>
		</td>
		<td style="width:10%">
			<xsl:value-of select="refPEI"/>
		</td>
		<td style="width:30%">
			<xsl:value-of select="messageErreur"/>
		</td>
		<td style="width:50%">
			<xsl:value-of select="messageAction"/>
			<xsl:if test="*">
				<p>Informations sur la tentative de synchronisation</p>
				<ul>
					<li>Type de synchronisation vers REMOCRA: <xsl:value-of select="donnees/contexteVisite"/></li>
					<li>Date et heure dans  WatGIS: <xsl:value-of select="donnees/dateHeureVisite"/></li>
					<li>Motifs d'indisponibilités eventuels : <xsl:value-of select="donnees/motifs"/></li>
				</ul>
			</xsl:if>
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet> 
