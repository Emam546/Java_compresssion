package Tools;

import java.util.ArrayList;

public class BytesArr extends ArrayList<Byte> {
    public BytesArr(){
        super();
    }
    public BytesArr(Byte b) {
        super();
        this.add(b);
    }

    public Byte[] toArray() {
        Byte[] res = new Byte[this.size()];
        super.toArray(res);
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BytesArr) {
            BytesArr obj2 = (BytesArr) obj;
            if (obj2.size() != size())
                return false;
            for (int i = 0; i < size(); i++)
                if (get(i) != obj2.get(i))
                    return false;
            return true;
        }

        return false;
    }
    @Override
    public BytesArr clone() {
        // TODO Auto-generated method stub
        return (BytesArr)super.clone();
    }
}

