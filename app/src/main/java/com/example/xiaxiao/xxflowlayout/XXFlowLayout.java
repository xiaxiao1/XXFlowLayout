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
 * Created by xiaxiao on 2017/12/7.
 * 流式布局
 * 只使用一个RelativeLayout实现，相对使用LinearLayout嵌套Linearlayout这种方式，减少了一层布局，。只有两层
 * 通过设置各子View之间的相对布局时的参考关系来实现流式布局
 */

public class XXFlowLayout extends RelativeLayout{
    /*
    * 三种竖直对齐方式
    * */
    //正常模式，也就是顶部对齐
    public static final int VERTICAL_ALIGN_NORMAL=0;
    //竖直居中对齐
    public static final int VERTICAL_ALIGN_CENTER=1;
    //底部对齐
    public static final int VERTICAL_ALIGN_BASE_BOTTOM=2;

    /**
     * 对齐方式，通过 {@link #setVerticalAlignType(int) }方法设置
     */
    int verticalAlignType=1;
    //每一行水平间隔大小
    int horizontalSpace=10;
    //是否水平均匀平铺
    boolean horizontalUniformed=false;
    //装载所有子View组成的行
    List<LineGroup> lines=new ArrayList<>();
    //当前正在装填子View的行
    LineGroup lastLine=null;
    //父布局的宽度
    int parentWidth=0;
    //当前待装载的子View集合
    List<View> currentChilds = new ArrayList<>();
    //当前此RelativeLayout的子View集合
    List<View> allChildren = new ArrayList<>();
    /**
     * 当前删除的子View ，看 {@link #onViewRemoved(View)}
     */
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
        //不管什么方式向父布局中添加子View，都会经过这个方法，
        //如果是在布局文件中添加了多个子View,则会先对每一个子View执行一遍这个方法，然后再进行下一步
        //如果实在代码中添加子View，则每加一个，执行此方法，然后进行下一步
        allChildren.add(child);
        //将新加入的子View放在待处理的集合中
        currentChilds.add(child);
       log("on add");
    }
    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        log("on remove");
        deleteChild = child;
        allChildren.remove(child);
       //获取到当期啊删除的子View下标
        int i = getChildIndex(child);
        //通过下标定位到从哪一行开始重新计算此后的各子View之间的关系。
        //这一步主要是为了提高性能，被删除的view所在行的之前的行，他们装载的子View的关系是不需要变化的。避免了重复计算
        getCurrentDeleteLine(i);



    }

    /**
     * 计算子View当前的下标
     * @param child
     * @return the index of the child in the allChildren list
     */
    public int getChildIndex(View child) {
        for (int i=0;i<allChildren.size();i++) {
            if (allChildren.get(i)==child) {
                return i;
            }
        }
        //保险起见
        return 0;
    }

    /**
     * 定位到当前删除的item所在的LineGroup,重新开始计算在此之后的itemViews 排列,在删除view时调用的。
     * 本方法内会将待重新计算关系的views重新装入 {@link #currentChilds}中，
     * @param currentDeleteIndex 当前删除的view的下标
     * @return  the index of the last LineGroup that will not be resetted.
     */
    public int getCurrentDeleteLine(int currentDeleteIndex) {
        //因为下标从0开始，哈哈
        int all=-1;
        int lastLineIndex=0;
        //其余的LineGroup也需要从集合中删除，因为关系要重新计算
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
        lastLine = null;
        //如果是第一行，则没必要做保留操作，直接全部删除，重新计算
        if (lastLineIndex == 0) {
            lines.clear();
            currentChilds.addAll(allChildren);
            currentChilds.remove(deleteChild);
        } else {
            //将当前删除的view所在行及之后的所有行包含的view加入重新计算的集合
            //注意：此时，已被删除的view也加入了进来
            for (int j=lastLineIndex+1;j<lines.size();j++) {
                currentChilds.addAll(lines.get(j).childs);
                deletes.add(lines.get(j));
            }
            //把已删除的删除掉，帮你挥一挥衣袖，别留下一片云彩
            currentChilds.remove(deleteChild);
            //此后的行 也全部删除掉，
            lines.removeAll(deletes);
        }
        //集合清空，复用啊
        deletes.clear();
        return lastLineIndex;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        log("on onLayout");
        //如果当前有待处理关系的view，则开始处理
        if (currentChilds.size()>0) {
            for (View c:currentChilds) {
                //针对每一个，进行流式布局
                addFlowedView(c);
            }
            currentChilds.clear();
            invalidate();
        }


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
        //算出这个父布局内部可用宽度
        if (parentWidth==0) {
            parentWidth = this.getMeasuredWidth()-this.getPaddingLeft()-this.getPaddingRight();
        }
        if (parentWidth==0) {
            return;
        }
    }

    /**
     * 对当前view进行流式布局安排
     * @param child the new added view
     */
    public void addFlowedView( View child) {
        log("------------------------------addFlowedView");
        if (lastLine==null) {
            //如果最后一行是null,说明之前的行都装满了，那就再创建一行
            lastLine = new LineGroup();
            //为新行设置参考view，本行内的所有view从上下位置来说，都会在这个参考view下
            if (lines.size() > 0&&lines.get(lines.size() - 1).childs.size()>0) {
                    lastLine.previousLineFlag = lines.get(lines.size() - 1).childs.get(0);
            } else {
                lastLine.previousLineFlag=null;
            }
            lines.add(lastLine);
        }
        /**
         * 看看当前行是否能够装得下这个新child
         * 能不能装的下， 主要看 {@link LineGroup#accept(View)}同不同意了
         */
        if (!lastLine.accept(child)) {
            //如果不能，则说明，到目前为止，这一行已经算是装满了，那就再创建一个新的行
            lastLine=null;
            //递归调用自己
            addFlowedView(child);
        }
    }

    /**
     * 设置垂直布局方式
     * @param type 参数来自 {@link #VERTICAL_ALIGN_NORMAL},{@link #VERTICAL_ALIGN_BASE_BOTTOM}, {@link #VERTICAL_ALIGN_CENTER}
     */
    public void setVerticalAlignType(int type) {
        this.verticalAlignType = type;
    }

    /**
     * 设置水平间隔大小
     * @param horizontalSpace
     */
    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    //设置是否是水平均匀分布
    public void setHorizontalUniformed(boolean uniformed) {
        this.horizontalUniformed = uniformed;
    }
    public void log(String msg) {
        Log.i("xx",msg);
    }
    public void log(int msg) {
        Log.i("xx",msg+"");
    }




    class LineGroup{
        //本行包含的子View的集合
        List<View> childs;
        /**
         * 本行子View布局时参考的view,来自上一行
         * belowOf
         */
        View previousLineFlag;
        /**
         * view向左参考的view
         * rightOf
         */
        View priorView;

        //本行的最大高度，取决于最高的view
        int maxHeight=0;
        //本行的已占用的宽度，来自所有view的所占宽度的和
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

        /**
         * 计算新加入的view的宽度，同时计算他的高度，实时更新当前行的最大高度数值
         * @param child
         * @return view的宽度
         */
        public int getNewAddedWidth(View child) {
            params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            int w = params.leftMargin + params.rightMargin + child.getMeasuredWidth()+horizontalSpace;
            int h = params.bottomMargin + params.topMargin + child.getMeasuredHeight();
            //更新行的最大高度
            if (maxHeight<h) {
                maxHeight=h;
            }
            return w;
        }

        public boolean isEmpty() {
            return childs.size()==0;
        }


        /**
         * 设置view流式布局时的相对位置参数
         * 即 当前View是在哪一个的右边，哪一个的下面，有这两个参考，即可定位此View
         * @param child
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void flowLayout(View child) {
            params = (LayoutParams) child.getLayoutParams();
//            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (previousLineFlag != null) {
                if (previousLineFlag.getId() == View.NO_ID) {
                    int mid = previousLineFlag.generateViewId();
                    previousLineFlag.setId(mid);
                }
                params.addRule(RelativeLayout.BELOW, previousLineFlag.getId());
                //之所以要有remove是防止不同的rule之间的冲突和覆盖，主要是在删除view时的
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
            //计算玩参考关系后，再进行对齐样式的修改
            verticalAlignment();
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

        /**
         * 竖直对齐样式
         */
        public void verticalAlignment() {
            //水平均匀分布时，分配到每一个view的左右margin
            int space = (parentWidth - totalWidth) / childs.size()/2;
            for (View c:childs) {
                int h=this.maxHeight-c.getMeasuredHeight();
                if (h>0) {
                    if (verticalAlignType==VERTICAL_ALIGN_BASE_BOTTOM) {
                        //竖直底部对齐
                        baseLineBottom(c,h);
                    } else if (verticalAlignType == VERTICAL_ALIGN_CENTER) {
                        //竖直居中对齐
                        centerVertical(c,h);
                    } else {
                        //竖直顶部对齐
                        normalAlign(c,h);
                    }
                }
                //水平方向平铺
                horizontalUniform(c,space);
            }
        }

        public void baseLineBottom(View child,int addHeight) {
            RelativeLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.topMargin =addHeight;
            child.setLayoutParams(params);
        }

        public void centerVertical(View child, int addHeight) {
            RelativeLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.topMargin = addHeight/2;
            params.bottomMargin =  addHeight/2;
            child.setLayoutParams(params);
        }
        public void normalAlign(View child, int addHeight) {
            RelativeLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.bottomMargin = addHeight;
            child.setLayoutParams(params);
        }

        /**
         * 水平均匀平铺
         * @param child
         * @param space 分配到每个view的左右间隔大小
         */
        public void horizontalUniform(View child, int space) {
            RelativeLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            //如果设置了平铺，则均匀平铺，如果没有，则按用户设置的水平间隔大小来设置
            if (horizontalUniformed) {
                params.leftMargin = space+horizontalSpace/2;
                params.rightMargin = space+horizontalSpace/2;
            } else {
                params.rightMargin = horizontalSpace;
            }
            child.setLayoutParams(params);
        }

    }


    /**
     * 待用
     */
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
