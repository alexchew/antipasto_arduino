package antipasto.Plugins.Events;

import java.awt.AWTEvent;

import antipasto.Plugins.EditorContext;

public class EditorEvent extends AWTEvent
{
	
	public EditorEvent(EditorContext source, int id)
	{
		super(source,id);
	}
	
    public EditorContext getPageContext()
    {
       return (EditorContext) getSource();
    }
    
	public String toString()
	{
		int id = getID();
		String sid = String.valueOf(id);
		if      (id == ALLPLUGINSLOADED  ) sid = "ALLPLUGINSLOADED";
		else if (id == PAGEDESELECTED) sid = "PAGEDESELECTED";
		else if (id == PAGECLOSED    ) sid = "PAGECLOSED";
		else if (id == PAGEICONIZED  ) sid = "PAGEICONIZED";
		else if (id == PAGEDEICONIZED) sid = "PAGEDEICONIZED";
		else if (id == PAGEOPENED)     sid = "PAGEOPENED";
      return "EditorEvent: ID="+sid+" Source="+getSource();
	}
	private static int RESERVED_ID_MAX = 0;
	
	public static final int ALLPLUGINSLOADED   = RESERVED_ID_MAX + 2000;
	public static final int PAGEDESELECTED = RESERVED_ID_MAX + 2001;
	public static final int PAGECLOSED     = RESERVED_ID_MAX + 2002;
	public static final int PAGEICONIZED   = RESERVED_ID_MAX + 2003;
	public static final int PAGEDEICONIZED = RESERVED_ID_MAX + 2004;
	public static final int PAGEOPENED     = RESERVED_ID_MAX + 2005;
} 