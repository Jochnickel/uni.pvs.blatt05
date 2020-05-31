import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(final String[] args)
            throws SAXException, IOException, ParserConfigurationException, XPathExpressionException,
            TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {

        final var svgDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("picture.svg");

        removeSunRays(svgDocument);
        makeSunLate(svgDocument);
        replantTree(svgDocument);
        growGrass(svgDocument);

        final var optimusPrime = TransformerFactory.newInstance().newTransformer();
        // optimusPrime.setOutputProperty(OutputKeys.INDENT, "yes");
        optimusPrime.transform(new DOMSource(svgDocument), new StreamResult("modfiedPicture.svg"));

    }

    private static void growGrass(final Document svgDocument) throws XPathExpressionException {
        final var xPath = XPathFactory.newInstance().newXPath();
        final var strawFinder = xPath.compile("//g[@id='grass']/line");
        final var allStraws = (NodeList) strawFinder.evaluate(svgDocument, XPathConstants.NODESET);
        for (int i = allStraws.getLength() - 1; 0 <= i; i--) {
            final var oneStraw = (Element) allStraws.item(i);
            final var x1 = Integer.parseInt(oneStraw.getAttribute("x1"));
            final var x2 = Integer.parseInt(oneStraw.getAttribute("x2"));
            final var y1 = Integer.parseInt(oneStraw.getAttribute("y1"));
            final var y2 = Integer.parseInt(oneStraw.getAttribute("y2"));
            oneStraw.setAttribute("x2", String.valueOf(x2 + x2 - x1));
            oneStraw.setAttribute("y2", String.valueOf(y2 + y2 - y1));
        }
    }

    private static void replantTree(final Document svgDocument) throws XPathExpressionException {
        final var xPath = XPathFactory.newInstance().newXPath();
        final var treeFinder = xPath.compile("//g[@id='tree']");
        final var tree = (Element) treeFinder.evaluate(svgDocument, XPathConstants.NODE);
        final String TRANSFORM = "transform";
        tree.setAttribute(TRANSFORM, tree.getAttribute(TRANSFORM) + " translate(350,0)");
    }

    private static void makeSunLate(final Document svgDocument) throws XPathExpressionException {
        final var xPath = XPathFactory.newInstance().newXPath();
        final var borderFinder = xPath.compile("//g[@id='sun']/circle");
        final var border = (Element) borderFinder.evaluate(svgDocument, XPathConstants.NODE);
        border.setAttribute("stroke-width", "0");
        border.setAttribute("fill", "red");
    }

    private static void removeSunRays(final Document svgDocument) throws XPathExpressionException {
        final var xPath = XPathFactory.newInstance().newXPath();
        final var beamFinder = xPath.compile("//g[@id='sun']/line");
        for (;;) {
            final var beam = (Node) beamFinder.evaluate(svgDocument, XPathConstants.NODE);
            if (null == beam) {
                break;
            }
            final var sun = beam.getParentNode();
            sun.removeChild(beam);
        }
    }
}