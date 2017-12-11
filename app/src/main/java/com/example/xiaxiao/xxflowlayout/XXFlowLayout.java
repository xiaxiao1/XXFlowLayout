package com.example.xiaxiao.xxflowlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/7.
 */

public class XXFlowLayout extends RelativeLayout{
    List<LineGroup> lines=new ArrayList<>();
    LineGroup lastLine=null;
    int parentWidth=0;
    boolean finishLayout=false;
    List<View> currentChilds = new ArrayList<>();
    List<View> allChildren = new ArrayList<>();
    View deleteChild;
    public XXFlowLayout(Context context) {
        super(context);
    }

    public XXFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XXFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XXFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        allChildren.add(child);
//        addFlowedView(child);
//        finishLayout=false;
        currentChilds.add(child);
       log("on add");
    }
    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
//        finishLayout=false;
        log("on remove");
        deleteChild = child;
        allChildren.remove(child);
        /*currentChilds.addAll(allChildren);
        lines.clear();
        lastLine=null;*/
        int i = getChildIndex(child);
        getCurrentDeleteLine(i);



    }

    public int getChildIndex(View child) {
        for (int i=0;i<allChildren.size();i++) {
            if (allChildren.get(i)==child) {
//                allChildren.remove(child);
                return i;
            }
        }
        //保险起见
        return 0;
    }

    //定位到当前删除的item所在的LineGroup,重新开始计算在此之后的itemViews 排列
    public int getCurrentDeleteLine(int currentDeleteIndex) {
        int all=-1;
        int lastLineIndex=0;
        List<LineGroup> deletes = new ArrayList<>();
        for (int i=0;i<lines.size();i++) {

            all = all + lines.get(i).childs.size();
            if (all >= currentDeleteIndex) {
                lastLineIndex = i - 1;
                if (lastLineIndex<0) {
                    lastLineIndex=0;
                }
                break;
            }
        }
        log("lastLine index ==="+lastLineIndex);
//        lastLine = lines.get(lastLineIndex);
        lastLine = null;
        if (lastLineIndex == 0) {
            lines.clear();
            currentChilds.addAll(allChildren);
            currentChilds.remove(deleteChild);
        } else {
            for (int j=lastLineIndex+1;j<lines.size();j++) {
                currentChilds.addAll(lines.get(j).childs);
                deletes.add(lines.get(j));
            }
            currentChilds.remove(deleteChild);
            lines.removeAll(deletes);
        }

        deletes.clear();
        return lastLineIndex;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        log("on onLayout");

        if (currentChilds.size()>0) {
            for (View c:currentChilds) {
                addFlowedView(c);
            }
            currentChilds.clear();
            invalidate();
        }


    }

    public void log(String msg) {
        Log.i("xx",msg);
    }
    public void log(int msg) {
        Log.i("xx",msg+"");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("xx","onDraw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log("onMeasure:"+this.getMeasuredHeight());
        if (parentWidth==0) {
            parentWidth = this.getMeasuredWidth();
        }
        if (parentWidth==0) {
            return;
        }
    }

    public void addFlowedView( View child) {
        log("------------------------------addFlowedView");
        if (lastLine==null) {
            lastLine = new LineGroup();
            if (lines.size() > 0&&lines.get(lines.size() - 1).childs.size()>0) {
                    lastLine.previousLineFlag = lines.get(lines.size() - 1).childs.get(0);
            } else {
                lastLine.previousLineFlag=null;
            }
            lines.add(lastLine);
        }
        if (!lastLine.accept(child)) {
            lastLine=null;
            addFlowedView(child);
        }
    }






    class LineGroup{
        List<View> childs;
        View previousLineFlag;
        View priorView;

        int maxHeight=0;
        int totalWidth=0;
        RelativeLayout.LayoutParams params;

        public LineGroup() {
            childs = new ArrayList<>();
        }

        public int getLineHeight(View child) {
            params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            int i = params.bottomMargin + params.topMargin + child.getMeasuredHeight();
            return i;
        }
        public int getNewAddedWidth(View child) {
            params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            int w = params.leftMargin + params.rightMargin + child.getMeasuredWidth();
            int h = params.bottomMargin + params.topMargin + child.getMeasuredHeight();
            if (maxHeight<h) {
                maxHeight=h;
            }
            return w;
        }

        public boolean isEmpty() {
            return childs.size()==0;
        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void flowLayout(View child) {
            params = (LayoutParams) child.getLayoutParams();
            if (previousLineFlag != null) {
                if (previousLineFlag.getId() == View.NO_ID) {
                    int mid = previousLineFlag.generateViewId();
                    previousLineFlag.setId(mid);
                }
                params.addRule(RelativeLayout.BELOW, previousLineFlag.getId());
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                params.removeRule(RelativeLayout.BELOW);
            }

            if (priorView != null) {
                if (priorView.getId() == View.NO_ID) {
                    int mid = priorView.generateViewId();
                    priorView.setId(mid);
                }
                params.addRule(RelativeLayout.RIGHT_OF, priorView.getId());
                params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
                params.removeRule(RelativeLayout.RIGHT_OF);
            }

            priorView=child;
            child.setLayoutParams(params);
//            log("flowed child?");
        }

        public void flowLayouts() {
            if (childs.size()>0) {
                for (View c:childs) {
                    flowLayout(c);
                }
            }
            invalidate();
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private boolean accept(View child) {
            int newW=getNewAddedWidth(child);
            log(newW+"  parentWidth="+parentWidth);
            if (totalWidth + newW <= parentWidth) {
                childs.add(child);
                totalWidth = totalWidth + newW;
                flowLayout(child);
                return true;
            } else {
                if (childs.size() == 0) {
                    childs.add(child);
                    totalWidth = parentWidth;
                    flowLayout(child);
                    return true;
                } else {
                    return false;
                }
            }
        }

        public boolean accept(LineItem lineItem) {
            return accept(lineItem.getItemView());
        }

    }


    public class LineItem{
        private View itemView;
        private LineGroup mLineGroup;

        public LineItem(View view) {
            this.itemView = view;
        }

        public void setLineGroup(LineGroup lineGroup) {
            this.mLineGroup = lineGroup;
        }

        public View getItemView() {
            return itemView;
        }

        /*public void setItemView(View itemView) {
            this.itemView = itemView;
        }*/

        public LineGroup getLineGroup() {
            return mLineGroup;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void flowLayout() {
            RelativeLayout.LayoutParams params = (LayoutParams) itemView.getLayoutParams();
            View previousLineFlag = mLineGroup.previousLineFlag;
            View priorView = mLineGroup.priorView;
            if (previousLineFlag != null) {
                if (previousLineFlag.getId() == View.NO_ID) {
                    int mid = previousLineFlag.generateViewId();
                    previousLineFlag.setId(mid);
                }
                params.addRule(RelativeLayout.BELOW, previousLineFlag.getId());
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
            }

            if (priorView != null) {
                if (priorView.getId() == View.NO_ID) {
                    int mid = priorView.generateViewId();
                    priorView.setId(mid);
                }
                params.addRule(RelativeLayout.RIGHT_OF, priorView.getId());
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            }
            mLineGroup.priorView=this.itemView;
            this.itemView.setLayoutParams(params);
//            log("flowed child?");
        }

    }


}
