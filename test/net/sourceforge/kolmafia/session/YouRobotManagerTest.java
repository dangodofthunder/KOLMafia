package net.sourceforge.kolmafia.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.sourceforge.kolmafia.AscensionClass;
import net.sourceforge.kolmafia.AscensionPath.Path;
import net.sourceforge.kolmafia.KoLCharacter;
import net.sourceforge.kolmafia.RequestLogger;
import net.sourceforge.kolmafia.preferences.Preferences;
import net.sourceforge.kolmafia.request.CharPaneRequest;
import net.sourceforge.kolmafia.request.CharSheetRequest;
import net.sourceforge.kolmafia.request.GenericRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class YouRobotManagerTest {

  @BeforeAll
  private static void beforeAll() {
    // Simulate logging out and back in again.
    GenericRequest.passwordHash = "";
    KoLCharacter.reset("");
    KoLCharacter.reset("you robot manager user");
    KoLCharacter.setPath(Path.YOU_ROBOT);
    Preferences.saveSettingsToFile = false;
  }

  @AfterAll
  private static void afterAll() {
    Preferences.saveSettingsToFile = true;
  }

  @BeforeEach
  private void beforeEach() {
    Preferences.setInteger("statbotUses", 0);
    Preferences.setInteger("youRobotTop", 0);
    Preferences.setInteger("youRobotLeft", 0);
    Preferences.setInteger("youRobotRight", 0);
    Preferences.setInteger("youRobotBottom", 0);
    Preferences.setString("youRobotCPUUpgrades", "");
    KoLCharacter.setAvatar("");
    ChoiceManager.lastChoice = 0;
    ChoiceManager.lastChoice = 0;
  }

  static String loadHTMLResponse(String path) throws IOException {
    // Load the responseText from saved HTML file
    return Files.readString(Paths.get(path)).trim();
  }

  private void verifyNoAvatarOrProperties() {
    assertEquals(0, Preferences.getInteger("statbotUses"));
    assertEquals(0, Preferences.getInteger("youRobotTop"));
    assertEquals(0, Preferences.getInteger("youRobotLeft"));
    assertEquals(0, Preferences.getInteger("youRobotRight"));
    assertEquals(0, Preferences.getInteger("youRobotBottom"));
    String[] avatar = KoLCharacter.getAvatar();
    assertEquals(1, KoLCharacter.getAvatar().length);
    assertEquals("", avatar[0]);
  }

  private void verifyAvatarFromProperties() {

    String prefix = "otherimages/robot/";
    int top = Preferences.getInteger("youRobotTop");
    String topImage = prefix + "top" + top + ".png";
    int left = Preferences.getInteger("youRobotLeft");
    String leftImage = prefix + "left" + left + ".png";
    int right = Preferences.getInteger("youRobotRight");
    String rightImage = prefix + "right" + right + ".png";
    int bottom = Preferences.getInteger("youRobotBottom");
    String bottomImage = prefix + "bottom" + bottom + ".png";
    // We don't save the body in a property since it is constant
    // based on character type. The data files came from an AT
    int ascensionClass = AscensionClass.ACCORDION_THIEF.getId();
    String bodyImage = prefix + "body" + ascensionClass + ".png";

    String[] avatar = KoLCharacter.getAvatar();
    Set<String> images = new HashSet<>(Arrays.asList(avatar));
    assertEquals(5, images.size());
    assertTrue(images.contains(topImage));
    assertTrue(images.contains(leftImage));
    assertTrue(images.contains(rightImage));
    assertTrue(images.contains(bottomImage));
    assertTrue(images.contains(bodyImage));
  }

  @Test
  public void canFindAvatarOnCharSheet() throws IOException {
    String responseText = loadHTMLResponse("request/test_scrapheap_charsheet.html");

    // Verify that the properties and avatar are not set
    verifyNoAvatarOrProperties();

    CharSheetRequest.parseStatus(responseText);

    // Verify that the properties and avatar are now set
    verifyAvatarFromProperties();
  }

  @Test
  public void canFindAvatarOnCharPane() throws IOException {
    String responseText = loadHTMLResponse("request/test_scrapheap_charpane.html");

    // Verify that the properties and avatar are not set
    verifyNoAvatarOrProperties();

    CharPaneRequest.checkYouRobot(responseText);

    // Verify that the properties and avatar are now set
    verifyAvatarFromProperties();
  }

  @Test
  public void canFindAvatarOnReassemblyStationVisit() throws IOException {
    String responseText = loadHTMLResponse("request/test_scrapheap_reassembly_station.html");

    // Verify that the properties and avatar are not set
    verifyNoAvatarOrProperties();

    ChoiceManager.lastChoice = 1445;
    GenericRequest request = new GenericRequest("choice.php?forceoption=0");
    request.responseText = responseText;
    YouRobotManager.visitChoice(request);

    // Verify that the properties and avatar are now set
    verifyAvatarFromProperties();
  }

  @Test
  public void canRegisterRequests() throws IOException {
    String urlString = "choice.php?whichchoice=1445&show=cpus";
    String expected = "Inspecting CPU Upgrade options at the Reassembly Station.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?pwd&whichchoice=1445&part=cpus&show=cpus&option=2&p=robot_resist";
    expected = "Upgrading your CPU with Weather Control Algorithms for 40 energy.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?whichchoice=1445&show=top";
    expected = "Inspecting Top Attachment options at the Reassembly Station.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?pwd&whichchoice=1445&part=top&show=top&option=1&p=1";
    expected = "Installing Pea Shooter as your Top Attachment for 5 scrap.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?whichchoice=1445&show=left";
    expected = "Inspecting Left Arm options at the Reassembly Station.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?pwd&whichchoice=1445&part=left&show=left&option=1&p=4";
    expected = "Installing Vice Grips as your Left Arm for 15 scrap.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?whichchoice=1445&show=right";
    expected = "Inspecting Right Arm options at the Reassembly Station.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?pwd&whichchoice=1445&part=right&show=right&option=1&p=8";
    expected = "Installing Surplus Flamethrower as your Right Arm for 40 scrap.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?whichchoice=1445&show=bottom";
    expected = "Inspecting Propulsion System options at the Reassembly Station.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);

    urlString = "choice.php?pwd&whichchoice=1445&part=bottom&show=bottom&option=1&p=7";
    expected = "Installing Snowplow as your Propulsion System for 30 scrap.";
    assertTrue(YouRobotManager.registerRequest(urlString));
    assertEquals(expected, RequestLogger.previousUpdateString);
  }
}
