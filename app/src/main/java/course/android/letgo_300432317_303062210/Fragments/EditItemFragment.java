package course.android.letgo_300432317_303062210.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import course.android.letgo_300432317_303062210.Activities.HomeActivity;
import course.android.letgo_300432317_303062210.DB.MyInfoManager;
import course.android.letgo_300432317_303062210.Classes.Product;
import course.android.letgo_300432317_303062210.R;
import course.android.letgo_300432317_303062210.interfaces.CallBackListener;

public class EditItemFragment extends Fragment {

	//The Fragment show's the edit screen for each item

	//private EditText itemIdView = null;
	private EditText itemTitleView = null;
	private EditText itemDescriptionView = null;
	private EditText itemPriceView = null;
	private EditText itemLocationView = null;
	private CallBackListener callback = null;
	private int targetReqCode = 0;

	private boolean photoFlag=false;
	public Spinner categorySpinner;
	private EditText itemCategoryView = null;

	private Button itemSaveBtn = null;
	private ImageView itemImage1 = null;

	private Activity ctx = null;

	static final int REQUEST_TAKE_PHOTO = 111;
	private static final int PHOTO_W = 400;
	private static final int PHOTO_H = 400;

	protected static final String NEW_ITEM_TAG = "-111";
	private File output=null;

	private Button deleteItemButton = null;
	private Button addPhotoBtn = null;
	private boolean newFlag =false;

	protected boolean shouldAskPermissions() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	@TargetApi(23)
	protected void askPermissions() {
		String[] permissions = {
				"android.permission.READ_EXTERNAL_STORAGE",
				"android.permission.WRITE_EXTERNAL_STORAGE"
		};
		int requestCode = 200;
		requestPermissions(permissions, requestCode);
	}



	//creates and returns the view hierarchy associated with the fragment.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_item, container, false);

		itemTitleView = (EditText) rootView.findViewById(R.id.item_title_txt);
		itemDescriptionView = (EditText) rootView.findViewById(R.id.item_desc_txt);
		itemLocationView = (EditText) rootView.findViewById(R.id.item_location_txt);
		//itemIdView = (EditText) rootView.findViewById(R.id.item_id_txt);

		//String category=catSpi.getSelectedItem().toString();
		//itemCategoryView = (EditText) rootView.findViewById(R.id.category_spinner);

		itemPriceView = (EditText) rootView.findViewById(R.id.item_price_txt);


		itemSaveBtn = (Button) rootView.findViewById(R.id.save_item_btn);
		itemImage1 = (ImageView) rootView.findViewById(R.id.item_img1);

		addPhotoBtn  = (Button) rootView.findViewById(R.id.add_photo_btn);
		deleteItemButton  = (Button) rootView.findViewById(R.id.delete_item_btn);

		//coin spinner
		Spinner coinSpinner = (Spinner) rootView.findViewById(R.id.coin_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> coinadapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.coin_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		coinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		coinSpinner.setAdapter(coinadapter);

		//category spinner
		categorySpinner = (Spinner) rootView.findViewById(R.id.category_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.category_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(categoryadapter);

		ctx  = getActivity();

		itemSaveBtn.setOnClickListener(saveItemListener);
		addPhotoBtn.setOnClickListener(addPhotoListener);
		itemImage1.setOnClickListener(delImage1Listener);
		deleteItemButton.setOnClickListener(deleteItemListener );

		Product product = MyInfoManager.getInstance().getSelectedItem();
		if(product!=null){
			itemTitleView.setText(product.getTitle());
			itemDescriptionView.setText(product.getDescription());
			itemLocationView.setText(product.getLocation());
			//itemIdView.setVisibility(View.INVISIBLE);
			//String category = infoItem.getCategory();

			categorySpinner.setSelection(0);
			//itemCategoryView.setText(category);

			/*
			if(categorySpinner.getSelectedItem()!=null){
				String category = categorySpinner.getSelectedItem().toString();
				itemCategoryView.setText(category);
			}
			else{
				itemCategoryView.setText("not selected");
			}
*/

			itemPriceView.setText(product.getPrice());
			Bitmap img1 =product.getImage1();
			if(img1!=null){
				itemImage1.setImageBitmap(img1);
				itemImage1.setVisibility(View.VISIBLE);

			}
		}




		return rootView;




	}



	//delete image
	private OnClickListener delImage1Listener= new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			final String title = "Delete Photo";
			final String msg = "Do you want to delete this photo? ";

			new AlertDialog.Builder(ctx)
					.setTitle(title)
					.setMessage(msg)
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							Product infoItem = MyInfoManager.getInstance().getSelectedItem();
							if(infoItem!=null){

								infoItem.setImage1(null);
								itemImage1.setVisibility(View.GONE);
								itemImage1.setImageBitmap(null);
								itemImage1.destroyDrawingCache();
							}
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})
					.show();

		}
	};

	//Add photo
	private OnClickListener addPhotoListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (shouldAskPermissions()) {
				askPermissions();
			}

			Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String imageFileName = "img_" + timeStamp + ".jpeg";
			output=new File(dir, imageFileName);
			Uri photoURI = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".my.package.name.provider", output);
			i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


			startActivityForResult(i, REQUEST_TAKE_PHOTO);

		}
	};



	//Save item
	private OnClickListener saveItemListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			try {
				String userId=MyInfoManager.getInstance().getSelectedFolder().getName();
				//String id= itemIdView.getText().toString();
				String title=itemTitleView.getText().toString();
				String description = itemDescriptionView.getText().toString();
				String location = itemLocationView.getText().toString();
				String price= itemPriceView.getText().toString();
				String category = categorySpinner.getSelectedItem().toString();

				Product item =MyInfoManager.getInstance().getSelectedItem();
				//created item without pic
				if(item==null){
					item = new Product(title,description,price,location,category,userId);
					MyInfoManager.getInstance().createItem(item);
				}
				//in case the user took a photo
				else{

					String id2= MyInfoManager.getInstance().getSelectedItem().getId();
					if(item.getId().equals(NEW_ITEM_TAG)){
						item.setId("product" + System.currentTimeMillis());
					}else{
						item.setId(id2);
					}

					item.setTitle(title);
					item.setDescription(description);
					item.setPrice(price);
					item.setLocation(location);
					item.setCategory(category);
					item.setUserId(userId);


					if(photoFlag&&newFlag){
						MyInfoManager.getInstance().createItem(item);
					}
					else{
						MyInfoManager.getInstance().updateItem(item);
					}
				}


//				ItemListFragment itemlistfragment = new ItemListFragment();
//				FragmentManager fManager =ctx.getFragmentManager();
//				FragmentTransaction ft = fManager.beginTransaction();
//				ft.replace(R.id.content_frame, itemlistfragment);
//				ft.addToBackStack(null);
//				ft.commit();

				Toast.makeText(ctx,
						"Your item saved successfully!", Toast.LENGTH_LONG).show();


				Intent i= new Intent(ctx,HomeActivity.class);
				startActivity(i);

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	};

	//delete item
	private OnClickListener deleteItemListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			try {


				Product item =MyInfoManager.getInstance().getSelectedItem();
				if(item!=null){
					MyInfoManager.getInstance().deleteItem(item);
				}

				Toast.makeText(ctx,
						"Your item has been deleted!", Toast.LENGTH_LONG).show();


				Intent i= new Intent(ctx,HomeActivity.class);
				startActivity(i);

//				ItemListFragment itemlistfragment = new ItemListFragment();
//				FragmentManager fManager =ctx.getFragmentManager();
//				FragmentTransaction ft = fManager.beginTransaction();
//				ft.replace(R.id.content_frame, itemlistfragment);
//				ft.addToBackStack(null);
//				ft.commit();

			} catch (Throwable e) {
				e.printStackTrace();
			}

		}
	};



	/**
	 * The activity returns with the photo.
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {


			if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {


				Product product = MyInfoManager.getInstance().getSelectedItem();

				if(product == null){
					photoFlag=true;
					newFlag =true;
					product = new Product();
					product.setId(NEW_ITEM_TAG);
					MyInfoManager.getInstance().setSelectedItem(product);
				}


				Bitmap bitmap =  getScaledImageFromFilePath(output.getAbsolutePath());
				if(bitmap!=null){
					if(product!=null){

						itemImage1.setImageBitmap(bitmap);
						itemImage1.setVisibility(View.VISIBLE);
						product.setImage1(bitmap);
					}

				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}


	//Create Image from file path
	private Bitmap getScaledImageFromFilePath(String imagePath) {
		// Get the dimensions of the View
		Bitmap scaledBitmap = null;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

			Matrix matrix = new Matrix();
			//matrix.postRotate(90);

			Bitmap rotatedBitmap =  Bitmap.createScaledBitmap(bitmap, PHOTO_W, PHOTO_H, false);
			scaledBitmap = Bitmap.createBitmap(rotatedBitmap , 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return scaledBitmap;
	}

}

