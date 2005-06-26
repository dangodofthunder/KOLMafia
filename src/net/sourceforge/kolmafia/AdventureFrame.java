
/**
 * Copyright (c) 2005, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Copyright (c) 2003, Spellcast development team
 * http://spellcast.dev.java.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "Spellcast development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package net.sourceforge.kolmafia;

// layout
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;

// event listeners
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

// containers
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

// other imports
import java.util.Iterator;
import java.text.DecimalFormat;
import java.text.ParseException;
import net.java.dev.spellcast.utilities.SortedListModel;
import net.java.dev.spellcast.utilities.LockableListModel;
import net.java.dev.spellcast.utilities.JComponentUtilities;

/**
 * An extended <code>KoLFrame</code> which presents the user with the ability to
 * adventure in the Kingdom of Loathing.  As the class is developed, it will also
 * provide other adventure-related functionality, such as inventoryManage management
 * and mall purchases.  Its content panel will also change, pending the activity
 * executed at that moment.
 */

public class AdventureFrame extends KoLFrame
{
	private static final Color ERROR_COLOR = new Color( 255, 128, 128 );
	private static final Color ENABLED_COLOR = new Color( 128, 255, 128 );
	private static final Color DISABLED_COLOR = null;

	private JTabbedPane tabs;
	private JTextField inClosetField;
	private LockableListModel adventureList;

	private AdventureSelectPanel adventureSelect;
	private MallSearchPanel mallSearch;
	private RemoveEffectsPanel removeEffects;
	private SkillBuffPanel skillBuff;
	private HeroDonationPanel heroDonation;
	private MeatStoragePanel meatStorage;

	/**
	 * Constructs a new <code>AdventureFrame</code>.  All constructed panels
	 * are placed into their corresponding tabs, with the content panel being
	 * defaulted to the adventure selection panel.
	 *
	 * @param	client	Client/session associated with this frame
	 * @param	adventureList	Adventures available to the user
	 * @param	resultsTally	Tally of adventuring results
	 */

	public AdventureFrame( KoLmafia client, SortedListModel resultsTally )
	{
		super( "KoLmafia: " + ((client == null) ? "UI Test" : client.getLoginName()) +
			" (" + KoLRequest.getRootHostName() + ")", client );

		this.isEnabled = true;
		this.tabs = new JTabbedPane();
		this.adventureList = AdventureDatabase.getAsLockableListModel( client );

		this.adventureSelect = new AdventureSelectPanel( resultsTally );
		tabs.addTab( "Adventure Select", adventureSelect );

		this.mallSearch = new MallSearchPanel();
		tabs.addTab( "Mall of Loathing", mallSearch );

		this.heroDonation = new HeroDonationPanel();
		this.meatStorage = new MeatStoragePanel();
		this.removeEffects = new RemoveEffectsPanel();
		this.skillBuff = new SkillBuffPanel();

		JPanel otherStuffPanel = new JPanel();
		otherStuffPanel.setLayout( new BoxLayout( otherStuffPanel, BoxLayout.Y_AXIS ) );
		otherStuffPanel.add( skillBuff );
		otherStuffPanel.add( meatStorage );
		otherStuffPanel.add( heroDonation );
		otherStuffPanel.add( removeEffects );

		JScrollPane otherStuffScroller = new JScrollPane( otherStuffPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

		JComponentUtilities.setComponentSize( otherStuffScroller, 500, 300 );
		tabs.addTab( "Other Activities", otherStuffScroller );

		addCompactPane();
		getContentPane().add( tabs, BorderLayout.CENTER );
		contentPanel = adventureSelect;

		addWindowListener( new LogoutRequestAdapter() );
		updateDisplay( ENABLED_STATE, MoonPhaseDatabase.getMoonEffect() );
		addMenuBar();
	}

	public void updateDisplay( int displayState, String message )
	{
		super.updateDisplay( displayState, message );
		if ( contentPanel != adventureSelect )
			adventureSelect.setStatusMessage( displayState, message );
	}

	/**
	 * Auxilary method used to enable and disable a frame.  By default,
	 * this attempts to toggle the enable/disable status on all tabs
	 * and the view menu item, as well as the item manager if it's
	 * currently visible.
	 *
	 * @param	isEnabled	<code>true</code> if the frame is to be re-enabled
	 */

	public void setEnabled( boolean isEnabled )
	{
		this.isEnabled = isEnabled && !client.isBuffBotActive();

		if ( mailMenuItem != null )
			mailMenuItem.setEnabled( this.isEnabled );

		if ( adventureSelect != null )
			adventureSelect.setEnabled( this.isEnabled );

		if ( mallSearch != null )
			mallSearch.setEnabled( this.isEnabled );

		if ( heroDonation != null )
			heroDonation.setEnabled( this.isEnabled );

		if ( meatStorage != null )
			meatStorage.setEnabled( this.isEnabled );

		if ( skillBuff != null )
			skillBuff.setEnabled( this.isEnabled );

		if ( removeEffects != null )
			removeEffects.setEnabled( this.isEnabled );

		Iterator framesIterator = existingFrames.iterator();

		while ( framesIterator.hasNext() )
			((KoLFrame) framesIterator.next()).setEnabled( isEnabled );
	}

	/**
	 * Utility method used to add a menu bar to the <code>AdventureFrame</code>.
	 * The menu bar contains configuration options and the general license
	 * information associated with <code>KoLmafia</code>.  In addition, the
	 * method adds an item which allows the user to view their character sheet.
	 */

	private void addMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );

		JMenuItem mapItem = new JMenuItem( "Navigate Map", KeyEvent.VK_N );
		mapItem.addActionListener( new MiniBrowserListener( "main.php" ) );
		JMenuItem calendarItem = new JMenuItem( "Consult Oracle", KeyEvent.VK_C );
		calendarItem.addActionListener( new DisplayFrameListener( CalendarFrame.class ) );
		JMenuItem mallItem = new JMenuItem( "Manipulate Mall", KeyEvent.VK_M );
		mallItem.addActionListener( new DisplayFrameListener( StoreManageFrame.class ) );
		JMenuItem caseItem = new JMenuItem( "Yeti's Museum", KeyEvent.VK_Y );
		caseItem.addActionListener( new DisplayFrameListener( MuseumFrame.class ) );
		JMenuItem otoriItem = new JMenuItem( "Pwn Clan Otori!" );
		otoriItem.addActionListener( new OtoriBuffListener() );

		JMenu statusMenu = addStatusMenu( menuBar );
		statusMenu.add( mapItem, 0 );
		statusMenu.add( calendarItem, 1 );
		statusMenu.add( mallItem );
		statusMenu.add( caseItem );
		statusMenu.add( otoriItem );

		JMenuItem foodItem = new JMenuItem( "Camping Routine", KeyEvent.VK_C );
		foodItem.addActionListener( new GetBreakfastListener() );
		JMenuItem buffbotMenuItem = new JMenuItem( "Evil BuffBot Mode", KeyEvent.VK_E );
		buffbotMenuItem.addActionListener( new DisplayFrameListener( BuffBotFrame.class ) );

		JMenu scriptMenu = addScriptMenu( menuBar );
		scriptMenu.add( foodItem, 3 );
		scriptMenu.add( buffbotMenuItem, 4 );

		JMenu visitMenu = new JMenu( "Travel" );
		visitMenu.setMnemonic( KeyEvent.VK_T );

		JMenuItem leaderItem = new JMenuItem( "Broken Records", KeyEvent.VK_B );
		leaderItem.addActionListener( new MiniBrowserListener( "records.php" ) );
		JMenuItem arenaItem = new JMenuItem( "Eat Cake-Arena", KeyEvent.VK_E );
		arenaItem.addActionListener( new DisplayFrameListener( CakeArenaFrame.class ) );
		JMenuItem hermitItem = new JMenuItem( "Hermit Hideout", KeyEvent.VK_H );
		hermitItem.addActionListener( new HermitRequestListener() );
		JMenuItem trapperItem = new JMenuItem( "Mountain Traps", KeyEvent.VK_M );
		trapperItem.addActionListener( new TrapperRequestListener() );
		JMenuItem hunterItem = new JMenuItem( "Seaside Towels", KeyEvent.VK_S );
		hunterItem.addActionListener( new HunterRequestListener() );
		JMenuItem hagnkItem = new JMenuItem( "Gnomish Storage", KeyEvent.VK_G );
		hagnkItem.addActionListener( new DisplayFrameListener( HagnkStorageFrame.class ) );

		visitMenu.add( leaderItem );
		visitMenu.add( arenaItem );
		visitMenu.add( hermitItem );
		visitMenu.add( trapperItem );
		visitMenu.add( hunterItem );
		visitMenu.add( hagnkItem );

		menuBar.add( visitMenu );

		JMenuItem clanItem = new JMenuItem( "Manage Your Clan", KeyEvent.VK_M );
		clanItem.addActionListener( new DisplayFrameListener( ClanManageFrame.class ) );

		JMenu peopleMenu = addPeopleMenu( menuBar );
		peopleMenu.add( clanItem );

		JMenuItem resetItem = new JMenuItem( "Reset Session", KeyEvent.VK_R );
		resetItem.addActionListener( new ResetSessionListener() );
		JMenuItem reloginItem = new JMenuItem( "Session Time-In", KeyEvent.VK_S );
		reloginItem.addActionListener( new SessionTimeInListener() );

		JMenu configMenu = addConfigureMenu( menuBar );
		configMenu.add( resetItem );
		configMenu.add( reloginItem );

		addHelpMenu( menuBar );
	}

	/**
	 * An internal class which represents the panel used for adventure
	 * selection in the <code>AdventureFrame</code>.
	 */

	private class AdventureSelectPanel extends KoLPanel
	{
		private JPanel actionStatusPanel;
		private JLabel actionStatusLabel;

		private JComboBox locationField;
		private JTextField countField;

		public AdventureSelectPanel( SortedListModel resultsTally )
		{
			super( "begin", "stop", new Dimension( 100, 20 ), new Dimension( 270, 20 ) );

			actionStatusPanel = new JPanel();
			actionStatusPanel.setLayout( new GridLayout( 2, 1 ) );

			actionStatusLabel = new JLabel( " ", JLabel.CENTER );
			actionStatusPanel.add( actionStatusLabel );
			actionStatusPanel.add( new JLabel( " ", JLabel.CENTER ) );

			locationField = new JComboBox( adventureList );
			countField = new JTextField();

			VerifiableElement [] elements = new VerifiableElement[3];
			elements[0] = new VerifiableElement( "Location: ", locationField );
			elements[1] = new VerifiableElement( "# of turnips: ", countField );
			elements[2] = new VerifiableElement( "Active Effects: ", new JComboBox(
				client == null || client.getCharacterData() == null ? new LockableListModel() :
					client.getCharacterData().getEffects().getMirrorImage() ) );

			setContent( elements, resultsTally );

			String lastAdventure = client == null ? "" : client.getSettings().getProperty( "lastAdventure" );

			for ( int i = 0; i < adventureList.size(); ++i )
				if ( adventureList.get(i).toString().equals( lastAdventure ) )
					locationField.setSelectedItem( adventureList.get(i) );
		}

		protected void setContent( VerifiableElement [] elements, LockableListModel resultsTally )
		{
			super.setContent( elements );

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout( new BorderLayout( 10, 10 ) );
			centerPanel.add( actionStatusPanel, BorderLayout.NORTH );
			centerPanel.add( new AdventureResultsPanel( resultsTally ), BorderLayout.CENTER );
			add( centerPanel, BorderLayout.CENTER );
			setDefaultButton( confirmedButton );
		}

		public void setStatusMessage( int displayState, String s )
		{
			String label = actionStatusLabel.getText();
			if ( !s.equals( "Timing-in session." ) &&
			     ( label.equals( "Session timed out." ) ||
			       label.equals( "Nightly maintenance." ) ))
				return;

			actionStatusLabel.setText( s );
			switch ( displayState )
			{
			case ERROR_STATE:
				sidePanel.setBackground( ERROR_COLOR );
				break;
			case ENABLED_STATE:
				if ( !isExecutingScript )
					sidePanel.setBackground( ENABLED_COLOR );
				break;
			case DISABLED_STATE:
				sidePanel.setBackground( DISABLED_COLOR );
				break;
			}
		}

		public void setEnabled( boolean isEnabled )
		{
			super.setEnabled( isEnabled );
			locationField.setEnabled( isEnabled );
			countField.setEnabled( isEnabled );
		}

		protected void actionConfirmed()
		{
			// Once the stubs are finished, this will notify the
			// client to begin adventuring based on the values
			// placed in the input fields.

			contentPanel = adventureSelect;
			Runnable request = (Runnable) locationField.getSelectedItem();

			client.getSettings().setProperty( "lastAdventure", request.toString() );
			client.getSettings().saveSettings();

			(new RequestThread( request, getValue( countField ) )).start();
		}

		protected void actionCancelled()
		{
			// Once the stubs are finished, this will notify the
			// client to terminate the loop early.  For now, since
			// there's no actual functionality, simply request focus

			contentPanel = adventureSelect;
			updateDisplay( ERROR_STATE, "Adventuring terminated." );
			client.cancelRequest();
			requestFocus();
		}

		public void requestFocus()
		{	locationField.requestFocus();
		}

		/**
		 * An internal class which represents the panel used for tallying the
		 * results in the <code>AdventureFrame</code>.  Note that all of the
		 * tallying functionality is handled by the <code>LockableListModel</code>
		 * provided, so this functions as a container for that list model.
		 */

		private class AdventureResultsPanel extends JPanel
		{
			public AdventureResultsPanel( LockableListModel resultsTally )
			{
				setLayout( new BorderLayout() );
				setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
				add( JComponentUtilities.createLabel( "Session Results Summary", JLabel.CENTER,
					Color.black, Color.white ), BorderLayout.NORTH );

				JList tallyDisplay = new JList( resultsTally );
				tallyDisplay.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
				tallyDisplay.setPrototypeCellValue( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
				tallyDisplay.setVisibleRowCount( 11 );

				add( new JScrollPane( tallyDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ), BorderLayout.CENTER );
			}
		}
	}

	/**
	 * An internal class which represents the panel used for mall
	 * searches in the <code>AdventureFrame</code>.
	 */

	private class MallSearchPanel extends KoLPanel
	{
		private boolean currentlyBuying;

		private JPanel actionStatusPanel;
		private JLabel actionStatusLabel;

		private JTextField searchField;
		private JTextField countField;
		private JCheckBox limitPurchasesField;

		private LockableListModel results;
		private JList resultsDisplay;

		public MallSearchPanel()
		{
			super( "search", "purchase", new Dimension( 100, 20 ), new Dimension( 250, 20 ) );
			setDefaultButton( confirmedButton );

			actionStatusPanel = new JPanel();
			actionStatusPanel.setLayout( new GridLayout( 2, 1 ) );

			actionStatusLabel = new JLabel( " ", JLabel.CENTER );
			actionStatusPanel.add( actionStatusLabel );
			actionStatusPanel.add( new JLabel( " ", JLabel.CENTER ) );

			searchField = new JTextField();
			countField = new JTextField();
			limitPurchasesField = new JCheckBox();
			results = new LockableListModel();

			VerifiableElement [] elements = new VerifiableElement[3];
			elements[0] = new VerifiableElement( "Search String: ", searchField );
			elements[1] = new VerifiableElement( "Search Limit: ", countField );
			elements[2] = new VerifiableElement( "Limit Purchases: ", limitPurchasesField );

			setContent( elements );
			currentlyBuying = false;
		}

		protected void setContent( VerifiableElement [] elements )
		{
			super.setContent( elements, null, null, null, true, true );

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout( new BorderLayout( 10, 10 ) );
			centerPanel.add( actionStatusPanel, BorderLayout.NORTH );
			centerPanel.add( new SearchResultsPanel(), BorderLayout.CENTER );
			add( centerPanel, BorderLayout.CENTER );
			setDefaultButton( confirmedButton );
		}

		public void setStatusMessage( int displayState, String s )
		{
			String label = actionStatusLabel.getText();
			if ( !s.equals( "Timing-in session." ) &&
			     ( label.equals( "Session timed out." ) ||
			       label.equals( "Nightly maintenance." ) ))
				return;

			actionStatusLabel.setText( s );
			switch ( displayState )
			{
			case ERROR_STATE:
				sidePanel.setBackground( ERROR_COLOR );
				break;
			case ENABLED_STATE:
				sidePanel.setBackground( ENABLED_COLOR );
				break;
			case DISABLED_STATE:
				sidePanel.setBackground( DISABLED_COLOR );
				break;
			}
		}

		public void setEnabled( boolean isEnabled )
		{
			super.setEnabled( isEnabled );
			searchField.setEnabled( isEnabled );
			countField.setEnabled( isEnabled );
			limitPurchasesField.setEnabled( isEnabled );
			resultsDisplay.setEnabled( isEnabled );
		}

		protected void actionConfirmed()
		{
			contentPanel = mallSearch;

			int searchCount = getValue( countField, -1 );

			if ( searchCount == -1 )
				(new SearchMallRequest( client, searchField.getText(), results )).run();
			else
				(new SearchMallRequest( client, searchField.getText(), searchCount, results )).run();

			if ( results.size() > 0 )
				resultsDisplay.ensureIndexIsVisible( 0 );
		}

		protected void actionCancelled()
		{
			if ( currentlyBuying )
				return;

			contentPanel = mallSearch;

			MallPurchaseRequest currentRequest;
			client.resetContinueState();

			int maxPurchases = 0;

			try
			{
				maxPurchases = limitPurchasesField.isSelected() ?
					df.parse( JOptionPane.showInputDialog( "Maximum number of items to purchase?" ) ).intValue() : Integer.MAX_VALUE;
			}
			catch ( Exception e )
			{
			}

			Object [] purchases = resultsDisplay.getSelectedValues();
			for ( int i = 0; i < purchases.length && maxPurchases > 0 && client.permitsContinue(); ++i )
			{
				if ( purchases[i] instanceof MallPurchaseRequest )
				{
					currentRequest = (MallPurchaseRequest) purchases[i];

					// Keep track of how many of the item you had before
					// you run the purchase request

					AdventureResult oldResult = new AdventureResult( currentRequest.getItemName(), 0 );
					int oldResultIndex = client.getInventory().indexOf( oldResult );
					if ( oldResultIndex != -1 )
						oldResult = (AdventureResult) client.getInventory().get( oldResultIndex );

					currentRequest.setLimit( maxPurchases );
					currentRequest.run();

					// Calculate how many of the item you have now after
					// you run the purchase request

					int newResultIndex = client.getInventory().indexOf( oldResult );
					if ( newResultIndex != -1 )
					{
						AdventureResult newResult = (AdventureResult) client.getInventory().get( newResultIndex );
						maxPurchases -= newResult.getCount() - oldResult.getCount();
					}

					// Remove the purchase from the list!  Because you
					// have already made a purchase from the store

					if ( client.permitsContinue() )
						results.remove( purchases[i] );
				}
			}

			if ( client.permitsContinue() )
				updateDisplay( ENABLED_STATE, "Purchases complete." );
			client.resetContinueState();
		}

		public void requestFocus()
		{	searchField.requestFocus();
		}

		/**
		 * An internal class which represents the panel used for tallying the
		 * results of the mall search request.  Note that all of the tallying
		 * functionality is handled by the <code>LockableListModel</code>
		 * provided, so this functions as a container for that list model.
		 */

		private class SearchResultsPanel extends JPanel
		{
			public SearchResultsPanel()
			{
				setLayout( new BorderLayout() );
				setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
				add( JComponentUtilities.createLabel( "Search Results", JLabel.CENTER,
					Color.black, Color.white ), BorderLayout.NORTH );

				resultsDisplay = new JList( results );
				resultsDisplay.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
				resultsDisplay.setPrototypeCellValue( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
				resultsDisplay.setVisibleRowCount( 11 );

				add( new JScrollPane( resultsDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ), BorderLayout.CENTER );
			}
		}
	}


	/**
	 * An internal class which represents the panel used for donations to
	 * the statues in the shrine.
	 */

	private class HeroDonationPanel extends LabeledKoLPanel
	{
		private JComboBox heroField;
		private JTextField amountField;

		public HeroDonationPanel()
		{
			super( "Donations to the Greater Good", "lump sum", "increments", new Dimension( 80, 20 ), new Dimension( 240, 20 ) );

			LockableListModel heroes = new LockableListModel();
			heroes.add( "Statue of Boris" );
			heroes.add( "Statue of Jarlsberg" );
			heroes.add( "Statue of Sneaky Pete" );

			heroField = new JComboBox( heroes );
			amountField = new JTextField();

			VerifiableElement [] elements = new VerifiableElement[2];
			elements[0] = new VerifiableElement( "Donate To: ", heroField );
			elements[1] = new VerifiableElement( "Amount: ", amountField );

			setContent( elements, true, true );
		}

		public void setEnabled( boolean isEnabled )
		{
			super.setEnabled( isEnabled );
			heroField.setEnabled( isEnabled );
			amountField.setEnabled( isEnabled );
		}

		protected void actionConfirmed()
		{
			contentPanel = heroDonation;

			if ( heroField.getSelectedIndex() != -1 )
				(new RequestThread( new HeroDonationRequest( client, heroField.getSelectedIndex() + 1, getValue( amountField ) ) )).start();

		}

		protected void actionCancelled()
		{
			try
			{
				contentPanel = heroDonation;
				int increments = df.parse( JOptionPane.showInputDialog( "How many increments?" ) ).intValue();

				if ( increments == 0 )
				{
					updateDisplay( ENABLED_STATE, "Donation cancelled." );
					return;
				}

				if ( heroField.getSelectedIndex() != -1 )
				{
					int eachAmount = getValue( amountField ) / increments;
					(new RequestThread( new HeroDonationRequest( client, heroField.getSelectedIndex() + 1, eachAmount ), increments )).start();
				}
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**
	 * An internal class which represents the panel used for storing and
	 * removing meat from the closet.
	 */

	private class MeatStoragePanel extends LabeledKoLPanel
	{
		private JTextField amountField;

		public MeatStoragePanel()
		{
			super( "Meat Management (Closet)", "deposit", "withdraw", new Dimension( 80, 20 ), new Dimension( 240, 20 ) );

			amountField = new JTextField();
			inClosetField = new JTextField( df.format( client == null ? 0 : client.getCharacterData().getClosetMeat() ) );

			VerifiableElement [] elements = new VerifiableElement[1];
			elements[0] = new VerifiableElement( "Transaction: ", amountField );
			setContent( elements, true, true );
		}

		public void setEnabled( boolean isEnabled )
		{
			super.setEnabled( isEnabled );
			amountField.setEnabled( isEnabled );
		}

		protected void actionConfirmed()
		{
			contentPanel = meatStorage;
			(new RequestThread( new ItemStorageRequest( client, getValue( amountField ), ItemStorageRequest.MEAT_TO_CLOSET ) )).start();
		}

		protected void actionCancelled()
		{
			contentPanel = meatStorage;
			(new RequestThread( new ItemStorageRequest( client, getValue( amountField ), ItemStorageRequest.MEAT_TO_INVENTORY ) )).start();
		}
	}

	/**
	 * An internal class which represents the panel used for removing
	 * effects from the character.
	 */

	private class RemoveEffectsPanel extends LabeledKoLPanel
	{
		private JComboBox effects;

		public RemoveEffectsPanel()
		{
			super( "Uneffective", "uneffect", "kill hermit", new Dimension( 80, 20 ), new Dimension( 240, 20 ) );

			effects = new JComboBox( client == null ? new LockableListModel() :
				client.getCharacterData().getEffects().getMirrorImage() );

			VerifiableElement [] elements = new VerifiableElement[1];
			elements[0] = new VerifiableElement( "Effects: ", effects );
			setContent( elements, true, true );
		}

		protected void actionConfirmed()
		{
			contentPanel = removeEffects;
			AdventureResult effect = (AdventureResult) effects.getSelectedItem();

			if ( effect == null )
				return;

			(new RequestThread( new UneffectRequest( client, effect ) )).start();
		}

		protected void actionCancelled()
		{
			contentPanel = removeEffects;
			updateDisplay( ERROR_STATE, "Unfortunately, you do not have a Valuable Trinket Crossbow." );
		}
	}

	/**
	 * An internal class which represents the panel used for adding
	 * effects to a character (yourself or others).
	 */

	private class SkillBuffPanel extends LabeledKoLPanel
	{
		private JComboBox skillSelect;
		private JTextField targetField;
		private JTextField countField;

		public SkillBuffPanel()
		{
			super( "Got Skills?", "cast buff", "maxbuff", new Dimension( 80, 20 ), new Dimension( 240, 20 ) );

			skillSelect = new JComboBox( client == null ? new LockableListModel() :
				client.getCharacterData().getAvailableSkills() );

			targetField = new JTextField();
			countField = new JTextField();

			VerifiableElement [] elements = new VerifiableElement[3];
			elements[0] = new VerifiableElement( "Skill Name: ", skillSelect );
			elements[1] = new VerifiableElement( "The Victim: ", targetField );
			elements[2] = new VerifiableElement( "# of Times: ", countField );
			setContent( elements, true, true );
		}

		protected void actionConfirmed()
		{
			contentPanel = skillBuff;
			(new RequestThread( getRequests( false ) )).start();
		}

		protected void actionCancelled()
		{
			contentPanel = skillBuff;
			(new RequestThread( getRequests( true ) )).start();
		}

		private Runnable [] getRequests( boolean maxBuff )
		{
			String buffName = ((UseSkillRequest) skillSelect.getSelectedItem()).getSkillName();
			if ( buffName == null )
				return null;

			String [] targets = targetField.getText().split( "," );
			for ( int i = 0; i < targets.length; ++i )
				targets[i] = targets[i].trim();

			for ( int i = 0; i < targets.length; ++i )
				if ( targets[i] != null )
					for ( int j = i + 1; j < targets.length; ++j )
						if ( targets[j] == null || targets[i].equals( targets[j] ) )
							targets[j] = null;

			int buffCount = maxBuff ?
				(int) ( client.getCharacterData().getCurrentMP() /
					ClassSkillsDatabase.getMPConsumptionByID( ClassSkillsDatabase.getSkillID( buffName ) ) ) : getValue( countField, 1 );

			if ( targets.length == 0 )
			{
				Runnable [] requests = new Runnable[1];
				requests[0] = new UseSkillRequest( client, buffName, "", buffCount );
				return requests;
			}

			Runnable [] requests = new Runnable[ targets.length ];
			for ( int i = 0; i < requests.length; ++i )
				if ( targets[i] != null )
					requests[i] = new UseSkillRequest( client, buffName, targets[i], buffCount );

			return requests;
		}
	}

	private class ResetSessionListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	client.resetSessionTally();
		}
	}

	/**
	 * In order to keep the user interface from freezing (or at least
	 * appearing to freeze), this internal class is used to process
	 * the request for fetching breakfast.
	 */

	private class GetBreakfastListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new GetBreakfastThread()).start();
		}

		private class GetBreakfastThread extends DaemonThread
		{
			public void run()
			{
				client.getBreakfast();
				updateDisplay( ENABLED_STATE, "Breakfast retrieved." );
			}
		}
	}

	/**
	 * An internal class used to handle logout whenever the window
	 * is closed.  An instance of this class is added to the window
	 * listener list.
	 */

	private class LogoutRequestAdapter extends WindowAdapter
	{
		public void windowOpened( WindowEvent e )
		{
			if ( client != null && client.getSettings().getProperty( "savePositions" ).equals( "true" ) )
			{
				Class currentClass;
				String [] framesToReload = client.getSettings().getProperty( "reloadFrames" ).split( "," );

				if ( framesToReload[0].length() > 0 )
				{
					KoLmafia [] parameters = new KoLmafia[1];
					parameters[0] = client;

					for ( int i = 0; i < framesToReload.length; ++i )
					{
						try
						{
							currentClass = Class.forName( "net.sourceforge.kolmafia." + framesToReload[i] );
							SwingUtilities.invokeLater( new CreateFrameRunnable( currentClass, parameters ) );
						}
						catch ( Exception e1 )
						{
							// If the exception gets thrown, then the
							// frame isn't created, which is just as
							// expected.  Do nothing.
						}
					}
				}
			}
		}

		public void windowClosed( WindowEvent e )
		{
			if ( client != null )
			{
				// Create a new instance of a client, and allow
				// the current client to run down in a separate
				// Thread.

				Iterator frames = existingFrames.iterator();
				KoLFrame currentFrame;

				StringBuffer framesToReload = new StringBuffer();
				boolean reloadFrame;

				while ( frames.hasNext() )
				{
					currentFrame = (KoLFrame) frames.next();
					reloadFrame = currentFrame.isShowing();
					currentFrame.setVisible( false );
					currentFrame.dispose();

					if ( reloadFrame && framesToReload.indexOf( currentFrame.getFrameName() ) == -1 )
					{
						if ( framesToReload.length() > 0 )
							framesToReload.append( ',' );
						framesToReload.append( currentFrame.getFrameName() );
					}
				}

				client.getSettings().setProperty( "reloadFrames", framesToReload.toString() );
				client.getSettings().saveSettings();

				existingFrames.clear();
				client.deinitialize();

				(new RequestThread( new LogoutRequest( client ) )).start();
				KoLmafiaGUI.main( new String[0] );
			}
		}
	}

	private class HermitRequestListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new RequestThread( new KoLAdventure( client, "hermit.php", "", "The Hermitage" ) )).start();
		}
	}

	private class TrapperRequestListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new RequestThread( new KoLAdventure( client, "trapper.php", "", "The 1337 Trapper" ) )).start();
		}
	}

	private class HunterRequestListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new RequestThread( new KoLAdventure( client, "town_wrong.php", "bountyhunter", "The Bounty Hunter" ) )).start();
		}
	}

	private class OtoriBuffListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new OtoriBuffThread()).start();
		}

		private class OtoriBuffThread extends DaemonThread
		{
			public void run()
			{	client.pwnClanOtori();
			}
		}
	}

	private class SessionTimeInListener implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{	(new SessionTimeInThread()).start();
		}

		private class SessionTimeInThread extends DaemonThread
		{
			public void run()
			{
				client.updateDisplay( NOCHANGE, "Timing-in session." );
				LoginRequest loginRequest = client.loginRequest;
				client.deinitialize();
				client.loginRequest = loginRequest;

				loginRequest.run();
				adventureList.clear();
				adventureList.addAll( AdventureDatabase.getAsLockableListModel( client ) );
				client.updateDisplay( ENABLED_STATE, "Session timed in." );
			}
		}
	}

	/**
	 * The main method used in the event of testing the way the
	 * user interface looks.  This allows the UI to be tested
	 * without having to constantly log in and out of KoL.
	 */

	public static void main( String [] args )
	{
		KoLFrame uitest = new AdventureFrame( null, new SortedListModel() );
		uitest.pack();  uitest.setVisible( true );  uitest.requestFocus();
	}
}
