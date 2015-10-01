package mbad7090.xml;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Rajah on 9/23/2015.
 */
public class PatentXMLMapReduceTest {

    private static final String testXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE us-patent-application SYSTEM \"us-patent-application-v42-2006-08-23.dtd\" [ ]>\n" +
            "<us-patent-application lang=\"EN\" dtd-version=\"v4.2 2006-08-23\" file=\"US20100281592A1-20101111.XML\" status=\"PRODUCTION\" id=\"us-patent-application\" country=\"US\" date-produced=\"20101027\" date-publ=\"20101111\">\n" +
            "<us-bibliographic-data-application lang=\"EN\" country=\"US\">\n" +
            "<publication-reference>\n" +
            "<document-id>\n" +
            "<country>US</country>\n" +
            "<doc-number>20100281592</doc-number>\n" +
            "<kind>A1</kind>\n" +
            "<date>20101111</date>\n" +
            "</document-id>\n" +
            "</publication-reference>\n" +
            "<application-reference appl-type=\"utility\">\n" +
            "<document-id>\n" +
            "<country>US</country>\n" +
            "<doc-number>12437946</doc-number>\n" +
            "<date>20090508</date>\n" +
            "</document-id>\n" +
            "</application-reference>\n" +
            "<us-application-series-code>12</us-application-series-code>\n" +
            "<classifications-ipcr>\n" +
            "<classification-ipcr>\n" +
            "<ipc-version-indicator><date>20060101</date></ipc-version-indicator>\n" +
            "<classification-level>A</classification-level>\n" +
            "<section>F</section>\n" +
            "<class>41</class>\n" +
            "<subclass>H</subclass>\n" +
            "<main-group>1</main-group>\n" +
            "<subgroup>02</subgroup>\n" +
            "<symbol-position>F</symbol-position>\n" +
            "<classification-value>I</classification-value>\n" +
            "<action-date><date>20101111</date></action-date>\n" +
            "<generating-office><country>US</country></generating-office>\n" +
            "<classification-status>B</classification-status>\n" +
            "<classification-data-source>H</classification-data-source>\n" +
            "</classification-ipcr>\n" +
            "<classification-ipcr>\n" +
            "<ipc-version-indicator><date>20060101</date></ipc-version-indicator>\n" +
            "<classification-level>A</classification-level>\n" +
            "<section>A</section>\n" +
            "<class>41</class>\n" +
            "<subclass>D</subclass>\n" +
            "<main-group>13</main-group>\n" +
            "<subgroup>05</subgroup>\n" +
            "<symbol-position>L</symbol-position>\n" +
            "<classification-value>I</classification-value>\n" +
            "<action-date><date>20101111</date></action-date>\n" +
            "<generating-office><country>US</country></generating-office>\n" +
            "<classification-status>B</classification-status>\n" +
            "<classification-data-source>H</classification-data-source>\n" +
            "</classification-ipcr>\n" +
            "<classification-ipcr>\n" +
            "<ipc-version-indicator><date>20060101</date></ipc-version-indicator>\n" +
            "<classification-level>A</classification-level>\n" +
            "<section>A</section>\n" +
            "<class>41</class>\n" +
            "<subclass>D</subclass>\n" +
            "<main-group>1</main-group>\n" +
            "<subgroup>14</subgroup>\n" +
            "<symbol-position>L</symbol-position>\n" +
            "<classification-value>I</classification-value>\n" +
            "<action-date><date>20101111</date></action-date>\n" +
            "<generating-office><country>US</country></generating-office>\n" +
            "<classification-status>B</classification-status>\n" +
            "<classification-data-source>H</classification-data-source>\n" +
            "</classification-ipcr>\n" +
            "<classification-ipcr>\n" +
            "<ipc-version-indicator><date>20060101</date></ipc-version-indicator>\n" +
            "<classification-level>A</classification-level>\n" +
            "<section>A</section>\n" +
            "<class>41</class>\n" +
            "<subclass>F</subclass>\n" +
            "<main-group>9</main-group>\n" +
            "<subgroup>00</subgroup>\n" +
            "<symbol-position>L</symbol-position>\n" +
            "<classification-value>I</classification-value>\n" +
            "<action-date><date>20101111</date></action-date>\n" +
            "<generating-office><country>US</country></generating-office>\n" +
            "<classification-status>B</classification-status>\n" +
            "<classification-data-source>H</classification-data-source>\n" +
            "</classification-ipcr>\n" +
            "</classifications-ipcr>\n" +
            "<classification-national>\n" +
            "<country>US</country>\n" +
            "<main-classification>  2  25</main-classification>\n" +
            "<further-classification>  2 22</further-classification>\n" +
            "<further-classification>  2211</further-classification>\n" +
            "<further-classification>  2338</further-classification>\n" +
            "</classification-national>\n" +
            "<invention-title id=\"d0e43\">LEG PROTECTOR AND PROTECTIVE SKIRT INCLUDING A LEG PROTECTOR</invention-title>\n" +
            "<parties>\n" +
            "<applicants>\n" +
            "<applicant sequence=\"00\" app-type=\"applicant-inventor\" designation=\"us-only\">\n" +
            "<addressbook>\n" +
            "<last-name>LEE</last-name>\n" +
            "<first-name>HENRY TAE JOON</first-name>\n" +
            "<address>\n" +
            "<city>Los Angeles</city>\n" +
            "<state>CA</state>\n" +
            "<country>US</country>\n" +
            "</address>\n" +
            "</addressbook>\n" +
            "<nationality>\n" +
            "<country>omitted</country>\n" +
            "</nationality>\n" +
            "<residence>\n" +
            "<country>US</country>\n" +
            "</residence>\n" +
            "</applicant>\n" +
            "</applicants>\n" +
            "<correspondence-address>\n" +
            "<addressbook>\n" +
            "<name>CLARK HILL PLC</name>\n" +
            "<address>\n" +
            "<address-1>150 NORTH MICHIGAN AVENUE, SUITE 2700</address-1>\n" +
            "<city>CHICAGO</city>\n" +
            "<state>IL</state>\n" +
            "<postcode>60601</postcode>\n" +
            "<country>US</country>\n" +
            "</address>\n" +
            "</addressbook>\n" +
            "</correspondence-address>\n" +
            "</parties>\n" +
            "</us-bibliographic-data-application>\n" +
            "<abstract id=\"abstract\">\n" +
            "<p id=\"p-0001\" num=\"0000\">A protective skirt for use in martial arts provides leg protectors for providing protection to the thighs of the user in the event of a full-forced attack to the thighs. The leg protectors are removable elements allowing the skirt to be used with out the leg protectors. Alternatively, the leg protectors can be integrated with the skirt.</p>\n" +
            "</abstract>\n" +
            "<drawings id=\"DRAWINGS\">\n" +
            "<figure id=\"Fig-EMI-D00000\" num=\"00000\">\n" +
            "<img id=\"EMI-D00000\" he=\"113.45mm\" wi=\"68.07mm\" file=\"US20100281592A1-20101111-D00000.TIF\"  img-content=\"drawing\" img-format=\"tif\"/>\n" +
            "</figure>\n" +
            "<figure id=\"Fig-EMI-D00001\" num=\"00001\">\n" +
            "<img id=\"EMI-D00001\" he=\"232.49mm\" wi=\"173.99mm\" file=\"US20100281592A1-20101111-D00001.TIF\"  img-content=\"drawing\" img-format=\"tif\"/>\n" +
            "</figure>\n" +
            "<figure id=\"Fig-EMI-D00002\" num=\"00002\">\n" +
            "<img id=\"EMI-D00002\" he=\"235.88mm\" wi=\"191.18mm\" file=\"US20100281592A1-20101111-D00002.TIF\"  img-content=\"drawing\" img-format=\"tif\"/>\n" +
            "</figure>\n" +
            "<figure id=\"Fig-EMI-D00003\" num=\"00003\">\n" +
            "<img id=\"EMI-D00003\" he=\"237.57mm\" wi=\"204.47mm\" file=\"US20100281592A1-20101111-D00003.TIF\"  img-content=\"drawing\" img-format=\"tif\"/>\n" +
            "</figure>\n" +
            "<figure id=\"Fig-EMI-D00004\" num=\"00004\">\n" +
            "<img id=\"EMI-D00004\" he=\"108.37mm\" wi=\"184.91mm\" file=\"US20100281592A1-20101111-D00004.TIF\"  img-content=\"drawing\" img-format=\"tif\"/>\n" +
            "</figure>\n" +
            "</drawings>\n" +
            "<description id=\"description\">\n" +
            "<?summary-of-invention description=\"Summary of Invention\" end=\"lead\"?>\n" +
            "<heading id=\"h-0001\" level=\"1\">FIELD OF THE INVENTION</heading>\n" +
            "<p id=\"p-0002\" num=\"0001\">This invention is generally directed to a protective device for use in connection with martial arts.</p>\n" +
            "<heading id=\"h-0002\" level=\"1\">BACKGROUND OF THE INVENTION</heading>\n" +
            "<p id=\"p-0003\" num=\"0002\">Often the practice of martial arts involves weapons lighting such as, for example, kendo (Japanese) and kumdo (Korean) sword fighting. In such practice, devices, for example, armor are used to protect the head, the body, and the hand/wrist. These devices allow the practitioner to practice the full extent of their art by allowing the practitioner to strike with full force while avoiding injury to the practitioner's partner. In the practice of sword fencing in kendo and kumdo for instance, a bamboo sword is used to strike the following areas: the head (mcn-Japanese/mori-Korean), the wrist (kote-Japanese/son-Korean), the waist (do-Japanese/hori-Korean), and the throat (tsuki-Japanese/mok-Korean) and the traditional armor <b>10</b> as shown in <figref idref=\"DRAWINGS\">FIG. 1</figref> is made to protect these areas. The traditional armor <b>10</b> generally includes a helmet <b>12</b>, a chest plate <b>14</b>, gloves <b>16</b>, and a skirt <b>20</b>. The skirt <b>20</b> protects the hips and legs in case of a slip from the strike to the waist. The skirt <b>20</b> (&#x201c;tare&#x201d; in Japanese and &#x201c;kapsang&#x201d; in Korean) is constructed from heavy canvas. Because the hips and thighs are not &#x201c;legal&#x201d; targets in sword fighting, the skirt provides only moderate protection but does not protect against a full-forced attack.</p>\n" +
            "<p id=\"p-0004\" num=\"0003\">The skirt <b>20</b> is shown in greater detail in <figref idref=\"DRAWINGS\">FIG. 2</figref>. The skirt <b>20</b> includes a belt <b>22</b> and a plurality of primary panels <b>24</b> and secondary panels <b>26</b> extending from the belt. The primary panels <b>24</b> overlap the secondary panels <b>26</b>. The skirt <b>20</b> includes ties <b>28</b> extending from either end of the belt <b>22</b>. To wear the skirt <b>20</b>, the user positions the belt <b>22</b> such that the secondary panels <b>26</b> are proximate the body. The belt <b>22</b> is wrapped around the user's waist to provide primary and secondary panels <b>24</b>, <b>26</b> extending in front of and behind the user. The ties <b>28</b> are crossed behind the user and then brought together in front of the user where they are tied together to secure the skirt <b>20</b> to the user. The secured ties <b>28</b> are positioned under one of the panels <b>24</b> of the skirt to avoid in advertent untying of the skirt <b>20</b>.</p>\n" +
            "<heading id=\"h-0003\" level=\"1\">SUMMARY OF THE INVENTION</heading>\n" +
            "<p id=\"p-0005\" num=\"0004\">Briefly, the present invention discloses a leg protector for attachment to the skirt <b>20</b> to provide a protective skirt including a leg protector which protects the thighs of the user from a full-forced attack during sword fighting.</p>\n" +
            "<?summary-of-invention description=\"Summary of Invention\" end=\"tail\"?>\n" +
            "<?brief-description-of-drawings description=\"Brief Description of Drawings\" end=\"lead\"?>\n" +
            "<description-of-drawings>\n" +
            "<heading id=\"h-0004\" level=\"1\">BRIEF DESCRIPTION OF THE DRAWINGS</heading>\n" +
            "<p id=\"p-0006\" num=\"0005\">The organization and manner of the structure and operation of the invention, together with further objects and advantages thereof, may best be understood by reference to the following description, taken in connection with the accompanying drawings, wherein like reference numerals identify like elements in which:</p>\n" +
            "<p id=\"p-0007\" num=\"0006\"><figref idref=\"DRAWINGS\">FIG. 1</figref> is a perspective view of prior art body armor;</p>\n" +
            "<p id=\"p-0008\" num=\"0007\"><figref idref=\"DRAWINGS\">FIG. 2</figref> is a perspective view of the skirt of the prior art body armor;</p>\n" +
            "<p id=\"p-0009\" num=\"0008\"><figref idref=\"DRAWINGS\">FIG. 3</figref> is a perspective view of the protective skirt;</p>\n" +
            "<p id=\"p-0010\" num=\"0009\"><figref idref=\"DRAWINGS\">FIG. 4</figref> is a front elevated view of a portion of the protective skirt;</p>\n" +
            "<p id=\"p-0011\" num=\"0010\"><figref idref=\"DRAWINGS\">FIG. 5</figref> is a rear elevated view of a portion of the protective skirt;</p>\n" +
            "<p id=\"p-0012\" num=\"0011\"><figref idref=\"DRAWINGS\">FIG. 6</figref> is front perspective view of the sock of the leg protector;</p>\n" +
            "<p id=\"p-0013\" num=\"0012\"><figref idref=\"DRAWINGS\">FIG. 7</figref> is a rear perspective view of the sock of the leg protector;</p>\n" +
            "<p id=\"p-0014\" num=\"0013\"><figref idref=\"DRAWINGS\">FIG. 8</figref> is a front elevated view of the shield of the leg protector;</p>\n" +
            "<p id=\"p-0015\" num=\"0014\"><figref idref=\"DRAWINGS\">FIG. 9</figref> is a rear elevated view of the shield of the leg protector;</p>\n" +
            "<p id=\"p-0016\" num=\"0015\"><figref idref=\"DRAWINGS\">FIG. 10</figref> is an elevated view showing attachment of the sock to the skirt;</p>\n" +
            "<p id=\"p-0017\" num=\"0016\"><figref idref=\"DRAWINGS\">FIG. 11</figref> is an elevated view showing the sock in its final position;</p>\n" +
            "<p id=\"p-0018\" num=\"0017\"><figref idref=\"DRAWINGS\">FIG. 12</figref> in a front elevated view showing attachment of the shield of the leg protector to the sock; and</p>\n" +
            "<p id=\"p-0019\" num=\"0018\"><figref idref=\"DRAWINGS\">FIG. 13</figref> is a rear elevated view of the protective skirt with the leg protector attached.</p>\n" +
            "</description-of-drawings>\n" +
            "<?brief-description-of-drawings description=\"Brief Description of Drawings\" end=\"tail\"?>\n" +
            "<?detailed-description description=\"Detailed Description\" end=\"lead\"?>\n" +
            "<heading id=\"h-0005\" level=\"1\">DETAILED DESCRIPTION OF THE ILLUSTRATED EMBODIMENT</heading>\n" +
            "<p id=\"p-0020\" num=\"0019\">While the invention may be susceptible to embodiment in different forms, there is shown in the drawings, and herein will be described in detail, a specific embodiment with the understanding that the present disclosure is to be considered an exemplification of the principles of the invention, and is not intended to limit the invention to that as illustrated and described herein.</p>\n" +
            "<p id=\"p-0021\" num=\"0020\">As shown in <figref idref=\"DRAWINGS\">FIG. 3</figref> the present invention provides a protective skirt <b>30</b> which provides protection of the user's legs during sword fighting or stick fighting sports in which the rules and method of play allow for full-forced strikes to the legs. A new sword fighting sport which allows for such full-forced strikes to the legs has been named by its creator as &#x201c;Gumtoogi&#x201d; and a new stick fighting sport allowing for such strikes has been named by its creator as &#x201c;Bongtoogi&#x201d;. The protective skirt <b>30</b> includes a left leg protector <b>32</b> and a right leg protector <b>34</b> each of which provides a strike panel to protect the users upper legs and in particular the thighs in the event of a full-forced attack, allowing these areas to become primary targets. The protective skirt <b>30</b> can be provided by adapting the skirt <b>20</b> shown in <figref idref=\"DRAWINGS\">FIG. 2</figref>.</p>\n" +
            "<p id=\"p-0022\" num=\"0021\">The protective skirt <b>30</b> is shown in greater detail in <figref idref=\"DRAWINGS\">FIGS. 4 and 5</figref>. As shown in <figref idref=\"DRAWINGS\">FIGS. 4 and 5</figref>, the protective skirt <b>30</b> is provided by modifying the skirt <b>20</b>. Each leg protector <b>32</b>, <b>34</b> includes a sock <b>36</b> and a shield <b>38</b>. The leg protectors <b>32</b>, <b>34</b> are identical to one another and therefore only the leg protector <b>32</b> will be described in detail with the understanding that the same description applies to the leg protector <b>34</b>.</p>\n" +
            "<p id=\"p-0023\" num=\"0022\">As shown in <figref idref=\"DRAWINGS\">FIGS. 6 and 7</figref>, the sock <b>36</b> is generally tubularly-shaped with a generally oval cross-section to generally provide a front portion <b>39</b> and a rear portion <b>40</b>. The sock <b>36</b> generally includes an outer surface <b>42</b>, an inner surface <b>44</b>, an upper end face <b>46</b>, and a lower end face <b>48</b>. A panel passage <b>50</b> is defined by the sock <b>36</b> for receiving a panel <b>24</b> of the skirt as will be described herein. An attachment mechanism <b>52</b> is provided on the outer surface <b>42</b> of the sock <b>36</b> at the front portion <b>39</b> and a pair of attachment mechanisms <b>54</b> is provided on the outer surface <b>42</b> of the sock <b>36</b> at the rear portion <b>40</b>. The attachment mechanisms <b>52</b>, <b>54</b> are for example, mating hook and loop type fasteners. The dimensions of the sock <b>36</b> will vary depending upon the size of the user. The dimension of the sock <b>36</b> may, for example, have a width of approximately 6&#x215e;&#x2033; and a height of approximately 10&#x215b;&#x2033;.</p>\n" +
            "<p id=\"p-0024\" num=\"0023\">The shield <b>38</b> is best shown in <figref idref=\"DRAWINGS\">FIGS. 8 and 9</figref>. The shield <b>38</b> generally includes a strike panel <b>60</b> and support straps <b>62</b>. The strike panel <b>60</b> is generally trapezoidally-shaped and includes a front surface <b>64</b>, a rear surface <b>66</b>, an upper end <b>68</b> and a lower end <b>70</b>. The width of the upper end <b>68</b> is smaller than the width of the lower end <b>70</b>. A plurality of slats <b>72</b> are mounted to the front surface <b>64</b> of the strike panel <b>60</b>. The slats <b>72</b> are made, for example, from bamboo or plastic. The slats <b>72</b> are attached to the strike panel <b>60</b> with, for example, leather string <b>74</b>. An attachment mechanism <b>76</b> such as, for example, a loop and hook-type fastener is provided on the rear surface <b>66</b> of the strike panel <b>60</b> for attachment of the shield <b>38</b> to the sock <b>36</b> as will be described herein. The shield is preferably formed from heavy cotton canvas and filed with padding. The dimensions of the strike panel will vary depending upon the size of the user. The dimensions of the strike panel may, for example have a width of approximately 6.5 to 6.75 inches at the upper end, a width of approximately 10&#x2033; to 10.75&#x2033; at the lower end, and a height of approximately 15.5&#x2033; to 18&#x2033;.</p>\n" +
            "<p id=\"p-0025\" num=\"0024\">The support straps <b>62</b> extend upwardly from the upper end <b>68</b> of the strike panel <b>60</b>. Lower ends <b>78</b> of the straps <b>62</b> are attached to the strike panel <b>60</b> and upper ends <b>80</b> of the straps are free. Attachment mechanisms <b>82</b> are provided on the rear surface of the upper ends <b>80</b> of the straps <b>62</b>. The attachment mechanisms <b>82</b> are, for example, hook and loop-type fasteners.</p>\n" +
            "<p id=\"p-0026\" num=\"0025\">As shown in <figref idref=\"DRAWINGS\">FIG. 10</figref>, attachment of the leg protectors <b>32</b> to skirt <b>20</b> begins by sliding a primary panel <b>24</b> of the skirt <b>20</b> in panel passage <b>50</b> of the sock <b>36</b>. The sock <b>36</b> is slid upwardly on the panel <b>24</b> until the upper end <b>46</b> of the sock <b>36</b> is proximate the belt <b>22</b> of the skirt <b>20</b>, as shown in <figref idref=\"DRAWINGS\">FIG. 11</figref>. The sock <b>36</b> is sized relative to the primary panel <b>24</b> such that a friction fit is provided between the inner surface <b>44</b> of the sock <b>36</b> and the panel <b>24</b> of the skirt <b>20</b>. The sock <b>36</b> is mounted to the panel <b>24</b> so that the attachment mechanism <b>52</b> of the front portion <b>39</b> of sock <b>36</b> is directed away from the user.</p>\n" +
            "<p id=\"p-0027\" num=\"0026\">Next, as shown in <figref idref=\"DRAWINGS\">FIG. 12</figref>, the shield <b>38</b> of the leg protector <b>32</b> is mounted to the sock <b>36</b> by mating the attachment mechanism <b>76</b> on the rear surface <b>66</b> of the leg protector <b>32</b> to the attachment mechanism <b>52</b> on the front portion <b>39</b> of the sock <b>36</b>. As shown in <figref idref=\"DRAWINGS\">FIG. 13</figref>, the support straps <b>62</b> are then wrapped over the belt <b>22</b> of the skirt <b>20</b> and downwardly. The attachment mechanisms at <b>82</b> the free ends <b>80</b> of the straps <b>62</b> are then mated with the attachment mechanisms <b>54</b> on the rear portion of the sock <b>36</b> to secure the shield <b>38</b> to the skirt <b>20</b>.</p>\n" +
            "<p id=\"p-0028\" num=\"0027\">Attachment of the leg protectors <b>32</b>, <b>34</b> to the skirt <b>20</b> therefore provides a protective skirt which not only protects the user from inadvertent hits to the legs, but also provides for protection from full-forced attacks to the legs. The skirt <b>30</b>, therefore includes protection to the upper legs as is necessary for the sport of Gumtoogi and Bongtoogi. In addition, the skirt <b>30</b> allows for removal of the leg protectors <b>32</b>, <b>34</b> should the user choose to remove them, for example for use in Kendo or Kumdo. The removable leg protectors <b>32</b>, <b>34</b> therefore eliminate the need for the user to purchase different skirts for each sport. Although hook and loop type fasteners and a sock <b>36</b> were described for attaching the shield of the leg protector to the skirt <b>20</b>, other methods of attaching the leg protector are within the scope of the invention. For example, snaps may be used to mate the shields <b>38</b> to the sock <b>36</b>. Alternatively the leg protectors could be tied to the skirt <b>20</b> to provide the protective skirt <b>30</b>.</p>\n" +
            "<p id=\"p-0029\" num=\"0028\">Although the skirt <b>30</b> has been described as including removable leg protectors <b>32</b>, <b>34</b>, it is to be understood that the leg protectors <b>32</b>, <b>34</b> can also be permanently integrated in the skirt <b>30</b>. In the case of permanent attachment of the leg protectors <b>32</b>, <b>34</b>, the sock <b>36</b> is not required and attachment mechanisms <b>52</b>, <b>54</b>, <b>76</b> and <b>82</b> are not required. Rather, the leg protectors <b>32</b>, <b>34</b> are permanently attached to the belt <b>22</b> of the skirt <b>30</b> by, for example, sewing the leg protector <b>32</b>, <b>34</b> to the belt <b>22</b> or to the panels <b>24</b> of the skirt <b>30</b>.</p>\n" +
            "<p id=\"p-0030\" num=\"0029\">Although the leg protectors have been described as being formed from heavy cotton canvas material, it is to be understood that a variety of materials can be used to form the leg protectors so long as the material serves to sufficiently protect the legs of the user from full-forced attacks to the legs, allowing the legs to become viable targets.</p>\n" +
            "<p id=\"p-0031\" num=\"0030\">While preferred embodiments of the present invention are shown and described, it is envisioned that those skilled in the art may devise various modifications of the present invention without departing from the spirit and scope of the appended claims.</p>\n" +
            "<?detailed-description description=\"Detailed Description\" end=\"tail\"?>\n" +
            "</description>\n" +
            "<us-claim-statement>The invention claimed is: </us-claim-statement>\n" +
            "<claims id=\"claims\">\n" +
            "<claim id=\"CLM-00001\" num=\"00001\">\n" +
            "<claim-text><b>1</b>. A leg protector for attachment to a panel of a skirt comprising;\n" +
            "<claim-text>a sleeve defining a channel for receiving the panel of the skirt, said sleeve including a front attachment mechanism and a rear attachment mechanism;</claim-text>\n" +
            "<claim-text>a leg shield including a strike panel having a front surface and a rear surface, an attachment mechanism on said rear surface of said strike panel, and an attachment strap including an attachment mechanism; and</claim-text>\n" +
            "<claim-text>wherein said attachment mechanism on said rear surface of said leg shield mates with said front attachment mechanism of said sleeve and said attachment mechanism of said strap mates with said rear attachment mechanism of said sleeve.</claim-text>\n" +
            "</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00002\" num=\"00002\">\n" +
            "<claim-text><b>2</b>. The leg protector of <claim-ref idref=\"CLM-00001\">claim 1</claim-ref>, wherein said strike panel is generally trapezoidally-shaped.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00003\" num=\"00003\">\n" +
            "<claim-text><b>3</b>. The leg protector of <claim-ref idref=\"CLM-00001\">claim 1</claim-ref>, wherein said strike panel of said leg shield further includes impact slats.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00004\" num=\"00004\">\n" +
            "<claim-text><b>4</b>. The leg protector of <claim-ref idref=\"CLM-00003\">claim 3</claim-ref>, wherein said impact slats are formed from bamboo.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00005\" num=\"00005\">\n" +
            "<claim-text><b>5</b>. The leg protector of <claim-ref idref=\"CLM-00001\">claim 1</claim-ref>, wherein said front attachment mechanism of said sleeve and said attachment mechanism on said rear surface of said strike panel are hook and loop type fasteners.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00006\" num=\"00006\">\n" +
            "<claim-text><b>6</b>. The leg protector of <claim-ref idref=\"CLM-00001\">claim 1</claim-ref> wherein said rear attachment mechanism of said sleeve and said attachment mechanism of said strap are hook and loop type fasteners.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00007\" num=\"00007\">\n" +
            "<claim-text><b>7</b>. The leg protector of <claim-ref idref=\"CLM-00001\">claim 1</claim-ref> wherein said channel of said sleeve is sized to provide a friction fit with the panel.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00008\" num=\"00008\">\n" +
            "<claim-text><b>8</b>. A protective skirt to be worn by a user comprising:\n" +
            "<claim-text>a belt;</claim-text>\n" +
            "<claim-text>a plurality of panels depending from said belt;</claim-text>\n" +
            "<claim-text>first and second strike panels depending from said bell for protection of the user's thighs from strikes; and</claim-text>\n" +
            "<claim-text>fastening straps.</claim-text>\n" +
            "</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00009\" num=\"00009\">\n" +
            "<claim-text><b>9</b>. The protective skirt of <claim-ref idref=\"CLM-00008\">claim 8</claim-ref>, wherein said strike panel further includes impact slats.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00010\" num=\"00010\">\n" +
            "<claim-text><b>10</b>. The protective skirt of <claim-ref idref=\"CLM-00008\">claim 8</claim-ref>, wherein said strike panels are removably attached to said skirt.</claim-text>\n" +
            "</claim>\n" +
            "<claim id=\"CLM-00011\" num=\"00011\">\n" +
            "<claim-text><b>11</b>. The protective skirt of <claim-ref idref=\"CLM-00010\">claim 10</claim-ref>, further comprising:\n" +
            "<claim-text>a first sleeve removeably attached to one of said plurality of panels and said first strike panel is removeably attached to said first sleeve; and</claim-text>\n" +
            "<claim-text>a second sleeve removeably attached to one of said plurality of panels and said second strike panel is removeably attached to said second sleeve. </claim-text>\n" +
            "</claim-text>\n" +
            "</claim>\n" +
            "</claims>\n" +
            "</us-patent-application>";

    private Mapper<LongWritable, Text, Text, Text> mapper;
    private MapDriver<LongWritable, Text, Text, Text> driver;

    @Before
    public void setUp() throws Exception {
        mapper = new PatentXMLMapReduce.Map();
        driver = new MapDriver<LongWritable, Text, Text, Text>(mapper);
    }

//    @Test
        @Ignore
    public void testRunJob() throws Exception {
        driver.withInput(new LongWritable(1L), new Text(testXml)).withOutput(new Text("foo"), new Text("bar")).runTest();
    }
}