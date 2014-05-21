''' This file has parameters to query stack overflow and produce summaries.
    Given a list of user IDs and a timeframe, it produces a summary of:
    1. reputation change during that time (per user)
    2. The number of questions asked
    3. The number of questions answered.
'''
import json
import urllib2,urllib
import time
import zlib
import pprint

base_url = 'http://api.stackexchange.com/2.2'

def requestStackOverflowData(url):
    req = urllib2.Request(url,headers={'Accept-Encoding': 'gzip,identity'})
    resp = urllib2.urlopen(req)
    text =  resp.read()
    gzip =("gzip" == resp.headers.getheader('Content-Encoding'))
    #print gzip
    if gzip:
        text = zlib.decompress(text,15+32)
    return json.loads(text)

def requestUsersData(uids,site):
    users = "%s/users/%s?order=desc&sort=reputation&site=%s"%(base_url,uids,site)
    return requestStackOverflowData(users)
    

def requestAllReputationPages(uids,from_dt,to_dt,site,page_size):
    more = True
    page=0
    items = {"items":[]}
    while (more):
        page=page+1
        reputation="%s/users/%s/reputation-history?fromdate=%s&todate=%s&site=%s&pagesize=%s&page=%s"%(base_url,uids,from_dt,to_dt,site,page_size,page)
        print reputation
        jRep = requestStackOverflowData(reputation)
        more = jRep["has_more"]
        items["items"].extend(jRep["items"])
    print "Number of pages:",page
    return items
        
    

def summary(uids,from_dt,to_dt,site="stackoverflow",page_size="100"):
    jRep = requestAllReputationPages(uids,from_dt,to_dt,site,page_size)
    jUsers = requestUsersData(uids,site)
    return addUsers(summarize(jRep),jUsers)
    
 
def summarize(aResp):
    items = aResp["items"]
    summ = {}
    pprint.pprint(aResp)
    for item in items:
        if item.has_key("reputation_change"):
            user_id = item["user_id"]
            reputation_change = item["reputation_change"]
            post_type = item["reputation_history_type"]
            if summ.has_key(user_id):
                summ[user_id]["reputation_change"] = summ[user_id]["reputation_change"]+int(reputation_change)
                summ[user_id][post_type] = summ[user_id].get(post_type,0)+1
            else:
                summ[user_id]={"reputation_change":reputation_change,post_type:1}
    return summ

def addUsers(jRepSum,jUsers):
    pprint.pprint(jRepSum)
    for user in jUsers["items"]:
        print user["user_id"],user["display_name"]
        if jRepSum.has_key(user["user_id"]):
            jRepSum[user["user_id"]]["display_name"]=user["display_name"]
        else:
            jRepSum[user["user_id"]]={"display_name":user["display_name"],"reputation_change":0}
    return jRepSum
    
 
def date2epoch(dt,dt_format="%m.%d.%Y %H:%M:%S"):
    return int(time.mktime(time.strptime(dt,dt_format)))
    
    
if __name__=='__main__':
    from_t=date2epoch("07.01.2013 00:00:00")
    to_t = date2epoch("08.01.2013 00:00:00")
    #print "CS347-S2014"
    #pprint.pprint( summary("1634666;1956256;1955944;3192092;2229847;3191522;3191650;2727084;3187763;2403309;3192094;1900408;2403302;3188062;3171412",from_t,to_t))
   
    print "CS207-2013"
    pprint.pprint(summary( "2403300;2403298;2403294;2403299;2403296;2403310;2403293;2403305;2403313;2403297;2403312;2403307;2403295;1985730;2403308;2403316;2403302;2403309;2403314;2079015;2403304;2403325;2406921;2407065;2585182",from_t,to_t))
   
    #print "CS207-Fall2012"
    #pprint.pprint(summary( "1631924;1629467;1634633;1634784;1634924;1634171;1633922;1637079;1647102;1732565",from_t,to_t))
   
   #print "CS207-Fall2012"
   #pprint.pprint(summary( "1956258;1956259;1956255;1956246;1956271;1234775;1956256;1956276;1956264;1956254;1956250;1956252;1956253;1956267;1956245;1956269;1956273;1956298;2172562",from_t,to_t))
   
   
