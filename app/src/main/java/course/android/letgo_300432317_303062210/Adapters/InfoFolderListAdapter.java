package course.android.letgo_300432317_303062210.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import course.android.letgo_300432317_303062210.DB.MyInfoManager;
import course.android.letgo_300432317_303062210.R;
import course.android.letgo_300432317_303062210.Classes.User;


import course.android.letgo_300432317_303062210.Adapters.ProductsDataAdapter;
import course.android.letgo_300432317_303062210.Classes.Product;
import course.android.letgo_300432317_303062210.DB.MyInfoManager;
import course.android.letgo_300432317_303062210.R;
import course.android.letgo_300432317_303062210.utils.NetworkResListener;
import course.android.letgo_300432317_303062210.utils.ResStatus;
import course.android.letgo_300432317_303062210.Fragments.EditItemFragment;
import course.android.letgo_300432317_303062210.interfaces.CallBackListener;
import course.android.letgo_300432317_303062210.utils.NetworkConnector;


public class InfoFolderListAdapter extends ArrayAdapter<User> {

	private Context context;
	private List<User> folders;

	//Constructor
	public InfoFolderListAdapter(Context context, int resource, List<User> objects) {
		super(context, resource, objects);
		this.context = context;
		this.folders = objects;
	}

	//Return number of folders
	@Override
	public int getCount() {
		return folders.size();
	}

	//Get a View that displays the data at the specified position in the data set.
	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View rootView = mInflater.inflate(R.layout.folder_list_item, null,false);
		final User currentFolder = getItem(position);
		TextView title = (TextView) rootView.findViewById(R.id.folderTitle);
		title.setText(currentFolder.getName());

		//Delete Folder
		ImageView deleteIcon = (ImageView) rootView.findViewById(R.id.deleteIcon);
		deleteIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String title = "Delete Folder";
				final String msg = "Are you sure? (all your data inside this folder will be gone)";
				AlertDialog.Builder  builder=  new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setMessage(msg);

				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						MyInfoManager.getInstance().deleteFolder(currentFolder);
						folders.remove(currentFolder);
            MyInfoManager.getInstance().deleteFolder(currentFolder);
						InfoFolderListAdapter.this.remove(currentFolder);
						InfoFolderListAdapter.this.notifyDataSetChanged();
					}
				});

				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

				AlertDialog alertDialog = builder.create();
				alertDialog .show();
			}
		});


		return rootView;
	}




	@Override
	public User getItem(int position) {
		return folders.get(position);
	}









}
